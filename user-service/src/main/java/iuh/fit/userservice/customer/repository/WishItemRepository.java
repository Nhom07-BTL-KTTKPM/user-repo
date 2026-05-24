package iuh.fit.userservice.customer.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import iuh.fit.userservice.customer.entity.WishItem;

public interface WishItemRepository extends JpaRepository<WishItem, UUID> {
    List<WishItem> findByCustomerId(UUID customerId);
    Optional<WishItem> findByCustomerIdAndProductId(UUID customerId, UUID productId);
}
