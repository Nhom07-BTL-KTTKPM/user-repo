package iuh.fit.userservice.customer.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import iuh.fit.userservice.customer.dto.CustomerUpdateRequest;
import iuh.fit.userservice.customer.entity.Customer;
import iuh.fit.userservice.customer.entity.Gender;
import iuh.fit.userservice.customer.repository.CustomerRepository;
import jakarta.transaction.Transactional;

@Service    
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomerById(String customerId) {
        return customerRepository.findById(UUID.fromString(customerId))
            .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
    }

    public Optional<Customer> getCustomerByAccountId(String accountId) {
        return customerRepository.findByAccountId(accountId);
    }

    public void updateCustomerInfo(String customerId, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(UUID.fromString(customerId))
            .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với id: " + customerId));    

        // Lấy dữ liệu từ object request
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setSkinType(request.getSkinType());
        
        customerRepository.save(customer);
    }

    @Transactional
    public void updateLoyaltyPoints(String customerId, int points) {
        Customer customer = customerRepository.findById(UUID.fromString(customerId))
            .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với id: " + customerId)); 

        int newPoints = customer.getLoyaltyPoints() + points;
        customer.setLoyaltyPoints(newPoints);
        customerRepository.save(customer);
    }

    
    public void addSkinConcern(String customerId, String skinConcern) {
        Customer customer = customerRepository.findById(UUID.fromString(customerId))
            .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với id: " + customerId));

        List<String> skinConcerns = customer.getSkinConcerns();
        if (!skinConcerns.contains(skinConcern)) {
            skinConcerns.add(skinConcern);
            customer.setSkinConcerns(skinConcerns);
            customerRepository.save(customer);
        }
    }
}