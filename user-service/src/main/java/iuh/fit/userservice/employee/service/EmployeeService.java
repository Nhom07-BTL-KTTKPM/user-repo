package iuh.fit.userservice.employee.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import feign.FeignException;
import iuh.fit.shared.api.ApiResponse;
import iuh.fit.shared.error.BusinessException;
import iuh.fit.shared.error.ErrorCode;
import iuh.fit.userservice.employee.dto.EmployeeCreateRequest;
import iuh.fit.userservice.employee.dto.RegisterRequest;
import iuh.fit.userservice.employee.dto.RegisterResponse;
import iuh.fit.userservice.employee.entity.Employee;
import iuh.fit.userservice.employee.repository.EmployeeRepository;
import iuh.fit.userservice.external.AuthClient;
import jakarta.transaction.Transactional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final AuthClient authClient;

    public EmployeeService(AuthClient authClient, EmployeeRepository employeeRepository) {
        this.authClient = authClient;
        this.employeeRepository = employeeRepository;
    }

    public Employee getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy nhân viên với id: " + id));
    }

    public Employee getEmployeeByAccountId(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "accountId không hợp lệ");
        }

        return employeeRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy nhân viên với accountId: " + accountId));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional
    public void createEmployee(EmployeeCreateRequest request) {
        try {
            RegisterRequest authRequest = new RegisterRequest(
                request.getEmail(),
                request.getPassword(),
                request.getFullName(),
                request.getPhoneNumber()
            );

            ApiResponse<RegisterResponse> apiResponse = authClient.createEmployeeAccount(authRequest);
    
            if (apiResponse == null || apiResponse.data() == null) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Không nhận được phản hồi từ Auth Service");
            }

            RegisterResponse authResponse = apiResponse.data();

            Employee employee = Employee.builder()
                    .accountId(authResponse.accountId().toString())
                    .employeeCode(generateEmployeeCode())
                    .fullName(request.getFullName())
                    .phoneNumber(request.getPhoneNumber())
                    .hireDate(LocalDate.now())
                    .isActive(true)
                    .build();

            employeeRepository.save(employee);
        } catch (FeignException.Conflict e) {
            throw new BusinessException(ErrorCode.CONFLICT, "Email này đã được sử dụng trong hệ thống");
        } catch (FeignException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Lỗi kết nối giữa các dịch vụ hệ thống");
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.CONFLICT, "Mã nhân viên đã tồn tại, vui lòng thử lại");
        }
    }

    public void updateEmployee(UUID id, EmployeeCreateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy nhân viên với id: " + id));
        try {
            employee.setFullName(request.getFullName());
            employee.setPhoneNumber(request.getPhoneNumber());
            employeeRepository.save(employee);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật thông tin nhân viên");
        }
    }

    public void deleteEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy nhân viên với id: " + id));
        try {
            employeeRepository.delete(employee);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Lỗi khi xóa nhân viên");
        }
    }

    public String generateEmployeeCode() {
        // Định dạng: EMP + yyMMddHHmmss
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        return "EMP" + timestamp;
    }
}
