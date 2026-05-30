package iuh.fit.userservice.employee.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import iuh.fit.userservice.employee.entity.Employee;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

	Optional<Employee> findByAccountId(String accountId);

}
