package iuh.fit.userservice.customer.repository;

import iuh.fit.userservice.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByAccountId(String accountId);
}
