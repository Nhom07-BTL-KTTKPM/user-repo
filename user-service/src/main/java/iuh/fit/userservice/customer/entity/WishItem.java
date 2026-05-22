package iuh.fit.userservice.customer.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wish_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishItem {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "customer_id", nullable = false, columnDefinition = "uuid")
    private UUID customerId;

    @Column(name = "created_at", nullable = false)
    private UUID productId;

}
