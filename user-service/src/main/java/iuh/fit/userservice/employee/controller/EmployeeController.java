package iuh.fit.userservice.employee.controller;

import iuh.fit.shared.api.ApiResponse;
import iuh.fit.shared.trace.TraceIdContext;
import iuh.fit.userservice.employee.dto.EmployeeCreateRequest;
import iuh.fit.userservice.employee.entity.Employee;
import iuh.fit.userservice.employee.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createEmployee(
            @Valid @RequestBody EmployeeCreateRequest request,
            HttpServletRequest servletRequest) {
        
        employeeService.createEmployee(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Nhân viên đã được tạo thành công", resolveTraceId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> getEmployeeById(
            @PathVariable UUID id) {
        
        Employee employee = employeeService.getEmployeeById(id);
        
        return ResponseEntity.ok(
                ApiResponse.success(employee, "Lấy thông tin nhân viên thành công", resolveTraceId()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Employee>>> getAllEmployees() {
        
        List<Employee> employees = employeeService.getAllEmployees();
        
        return ResponseEntity.ok(
                ApiResponse.success(employees, "Lấy danh sách nhân viên thành công", resolveTraceId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeCreateRequest request) {
        
        employeeService.updateEmployee(id, request);
        
        return ResponseEntity.ok(
                ApiResponse.success(null, "Cập nhật thông tin nhân viên thành công", resolveTraceId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable UUID id) {
        
        employeeService.deleteEmployee(id);
        
        return ResponseEntity.ok(
                ApiResponse.success(null, "Xóa nhân viên thành công", resolveTraceId()));
    }

    /**
     * Lấy Trace ID từ context để đồng bộ log và phản hồi
     */
    private String resolveTraceId() {
        return TraceIdContext.get();
    }
}