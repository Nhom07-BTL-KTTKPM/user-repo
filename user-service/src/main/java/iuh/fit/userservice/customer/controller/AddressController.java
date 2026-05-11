package iuh.fit.userservice.customer.controller;

import java.util.List;

import iuh.fit.shared.api.ApiResponse;
import iuh.fit.shared.trace.TraceIdContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import iuh.fit.userservice.customer.entity.Address;
import iuh.fit.userservice.customer.service.AddressService;

@RestController
@RequestMapping("/api/v1/user/addresses")
public class AddressController {

    private final AddressService addressService;
    
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    //lấy địa chỉ theo addressId
    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Address>> getAddressById(@PathVariable String addressId) {
        Address address = addressService.getAddressById(addressId);
        return ResponseEntity.ok(
                ApiResponse.success(address, "Lấy địa chỉ thành công", resolveTraceId())
        );
    }

    //lấy tất cả địa chỉ của khách hàng
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<Address>>> getAddressesByCustomerId(@PathVariable String customerId) {
        List<Address> addresses = addressService.getAddressByCustomerId(customerId);
        return ResponseEntity.ok(
                ApiResponse.success(addresses, "Lấy danh sách địa chỉ thành công", resolveTraceId())
        );
    }

    //lấy địa chỉ mặc định của khách hàng
    @GetMapping("/default/{customerId}")
    public ResponseEntity<ApiResponse<Address>> getDefaultAddressByCustomerId(@PathVariable String customerId) {
        Address address = addressService.getDefaultAddressByCustomerId(customerId);
        return ResponseEntity.ok(
                ApiResponse.success(address, "Lấy địa chỉ mặc định thành công", resolveTraceId())
        );
    }

    //thêm địa chỉ mới
    @PostMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Void>> addAddress(@PathVariable String customerId, @RequestBody Address address) {
        addressService.addAddress(customerId, address);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Thêm địa chỉ thành công", resolveTraceId()));
    }

    //cập nhật địa chỉ
    @PutMapping("/update/{addressId}")
    public ResponseEntity<ApiResponse<Void>> updateAddress(@PathVariable String addressId, @RequestBody Address address) {
        addressService.updateAddress(addressId, address);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Cập nhật địa chỉ thành công", resolveTraceId())
        );
    }

    //đặt địa chỉ mặc định
    @PutMapping("/default/{addressId}")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(@PathVariable String addressId) {
        addressService.setDefaultAddress(addressId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Đặt địa chỉ mặc định thành công", resolveTraceId())
        );
    }

    //xóa địa chỉ
    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable String addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Xóa địa chỉ thành công", resolveTraceId())
        );
    }

    private String resolveTraceId() {
        return TraceIdContext.get();
    }
}
