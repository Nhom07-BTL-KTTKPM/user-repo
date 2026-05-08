package iuh.fit.userservice.customer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Entity
@Table(name = "addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @UuidGenerator
    private UUID id;
    private String customerId;
    private String recipientName;
    private String phone;
    private String streetAddress;
    private String ward;
    private String district;
    private String city;
    private Boolean isDefault;

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
