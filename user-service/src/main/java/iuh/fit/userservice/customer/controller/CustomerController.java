package iuh.fit.userservice.customer.controller;

import iuh.fit.shared.api.ApiResponse;
import iuh.fit.shared.trace.TraceIdContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import iuh.fit.userservice.customer.dto.CustomerUpdateRequest;
import iuh.fit.userservice.customer.entity.Customer;
import iuh.fit.userservice.customer.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/v1/user/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    //Lấy thông tin khách hàng bằng customerId
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerById(@PathVariable String customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(
            ApiResponse.success(customer, "Lấy thông tin khách hàng thành công", resolveTraceId())
        );
    }

    //Lấy thông tin khách hàng bằng accountId
    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerByAccountId(@PathVariable String accountId) {
        Customer customer = customerService.getCustomerByAccountId(accountId);
        return ResponseEntity.ok(
            ApiResponse.success(customer, "Lấy thông tin khách hàng theo tài khoản thành công", resolveTraceId())
        );
    } 

    //cập nhật thông tin khách hàng
    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Void>> updateCustomer(@PathVariable String customerId, @RequestBody CustomerUpdateRequest request) {
        customerService.updateCustomerInfo(customerId, request);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Cập nhật thông tin khách hàng thành công", resolveTraceId())
        );
    }

    //thêm điểm
    @PutMapping("/update-points/{customerId}")
    public ResponseEntity<ApiResponse<Void>> updateLoyaltyPoints(@PathVariable String customerId, @RequestBody int points) {
        customerService.updateLoyaltyPoints(customerId, points);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Cập nhật điểm tích lũy thành công", resolveTraceId())
        );
    }

    //thêm mối quan tâm về da
    @PutMapping("/add-skin-concern/{customerId}")
    public ResponseEntity<ApiResponse<Void>> addSkinConcern(@PathVariable String customerId, @RequestBody String skinConcern) {
        String cleaned = skinConcern.replaceAll("^\"|\"$", "");
        customerService.addSkinConcern(customerId, cleaned);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Cập nhật mối quan tâm về da thành công", resolveTraceId())
        );
    }

    private String resolveTraceId() {
        return TraceIdContext.get();
    }
}
