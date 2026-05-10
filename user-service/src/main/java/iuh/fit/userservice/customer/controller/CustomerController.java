package iuh.fit.userservice.customer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import iuh.fit.userservice.customer.dto.CustomerUpdateRequest;
import iuh.fit.userservice.customer.entity.Customer;
import iuh.fit.userservice.customer.service.CustomerService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    //Lấy thông tin khách hàng bằng customerId
    @GetMapping("/{customerId}")
    public Customer getCustomerById(@PathVariable String customerId) {
        return customerService.getCustomerById(customerId);
    }

    //Lấy thông tin khách hàng bằng accountId
    @GetMapping("/account/{accountId}")
    public Customer getCustomerByAccountId(@PathVariable String accountId) {
        return customerService.getCustomerByAccountId(accountId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
    } 

    //cập nhật thông tin khách hàng
    @PutMapping("/{customerId}")
    public void updateCustomer(@PathVariable String customerId, @RequestBody CustomerUpdateRequest request) {
        customerService.updateCustomerInfo(customerId, request);
    }

    //thêm điểm
    @PutMapping("/update-points/{customerId}")
    public void updateLoyaltyPoints(@PathVariable String customerId, @RequestBody int points) {
        customerService.updateLoyaltyPoints(customerId, points);
    }

    //thêm mối quan tâm về da
    @PutMapping("/add-skin-concern/{customerId}")
    public void addSkinConcern(@PathVariable String customerId, @RequestBody String skinConcern) {
        String cleaned = skinConcern.replaceAll("^\"|\"$", "");
        customerService.addSkinConcern(customerId, cleaned);
    }
}
