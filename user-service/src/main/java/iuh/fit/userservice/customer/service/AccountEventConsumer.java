package iuh.fit.userservice.customer.service;

import iuh.fit.userservice.customer.entity.Customer;
import iuh.fit.userservice.customer.repository.CustomerRepository;
import iuh.fit.userservice.event.AccountRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import iuh.fit.userservice.external.CartServiceClient;

@Service
public class AccountEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AccountEventConsumer.class);

    private final CustomerRepository customerRepository;
    private final CartServiceClient cartServiceClient;

    public AccountEventConsumer(CustomerRepository customerRepository, CartServiceClient cartServiceClient) {
        this.customerRepository = customerRepository;
        this.cartServiceClient = cartServiceClient;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${user.rabbitmq.queue.account-created}", durable = "true"),
            exchange = @Exchange(value = "${user.rabbitmq.critical-exchange}", type = "direct"),
            key = "${user.rabbitmq.routing.account-created}"
    ))
    public void onAccountCreated(AccountRegisteredEvent event) {
        if (!"CUSTOMER".equalsIgnoreCase(event.role())) {
            log.info("[AccountEventConsumer] Skipping customer creation for role: {}", event.role());
            return; 
        }
        
        log.info("[AccountEventConsumer] Received AccountRegisteredEvent for accountId={}", event.accountId());
        try {
            customerRepository.findByAccountId(event.accountId()).ifPresentOrElse(
                    existing -> log.info("Customer already exists for accountId={}", event.accountId()),
                    () -> {
                        Customer customer = Customer.builder()
                                .accountId(event.accountId())
                                .email(event.email())
                                .fullName(event.fullName())
                                .phoneNumber(event.phoneNumber())
                                .build();
                        customerRepository.save(customer);
                        log.info("Successfully created Customer for accountId={}", event.accountId());
                        
                        try {
                            cartServiceClient.createCart(new CartServiceClient.CreateCartRequest(customer.getId().toString()));
                            log.info("Successfully requested Cart creation for customerId={}", customer.getId());
                        } catch (Exception ex) {
                            log.error("Failed to call cart-service for customerId={}", customer.getId(), ex);
                        }
                    }
            );
        } catch (Exception e) {
            log.error("Failed to process AccountRegisteredEvent for accountId={}: {}", event.accountId(), e.getMessage(), e);
        }
    }
}
