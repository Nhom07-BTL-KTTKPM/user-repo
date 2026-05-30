package iuh.fit.userservice.customer.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import iuh.fit.userservice.customer.entity.Address;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByCustomerId(String customerId);
} 
