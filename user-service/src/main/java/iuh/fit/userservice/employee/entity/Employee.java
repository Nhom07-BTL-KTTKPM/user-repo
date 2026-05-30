package iuh.fit.userservice.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employees")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @UuidGenerator
    private UUID id;
    private String accountId;
    @Column(unique = true, nullable = false)
    private String employeeCode;
    private String fullName;
    private String phoneNumber;
    private LocalDate hireDate;
    private boolean isActive;
}
