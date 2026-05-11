package iuh.fit.userservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import iuh.fit.shared.api.ApiResponse;
import iuh.fit.userservice.employee.dto.RegisterRequest;
import iuh.fit.userservice.employee.dto.RegisterResponse;

@FeignClient(name = "auth-service") 
public interface AuthClient {

    @PostMapping("/api/v1/auth/internal/register")
    ApiResponse<RegisterResponse> createEmployeeAccount(@RequestBody RegisterRequest request);
}