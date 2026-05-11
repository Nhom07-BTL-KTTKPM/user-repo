package iuh.fit.userservice.customer.controller;

import java.util.List;

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
    public Address getAddressById(@PathVariable String addressId) {
        return addressService.getAddressById(addressId);
    }

    //lấy tất cả địa chỉ của khách hàng
    @GetMapping("/customer/{customerId}")
    public List<Address> getAddressesByCustomerId(@PathVariable String customerId) {
        return addressService.getAddressByCustomerId(customerId);
    }

    //lấy địa chỉ mặc định của khách hàng
    @GetMapping("/default/{customerId}")
    public Address getDefaultAddressByCustomerId(@PathVariable String customerId) {
        return addressService.getDefaultAddressByCustomerId(customerId);
    }

    //thêm địa chỉ mới
    @PostMapping("/{customerId}")
    public void addAddress(@PathVariable String customerId, @RequestBody Address address) {
        addressService.addAddress(customerId, address);
    }

    //cập nhật địa chỉ
    @PutMapping("/update/{addressId}")
    public void updateAddress(@PathVariable String addressId, @RequestBody Address address) {
        addressService.updateAddress(addressId, address);
    }

    //đặt địa chỉ mặc định
    @PutMapping("/default/{addressId}")
    public void setDefaultAddress(@PathVariable String addressId) {
        addressService.setDefaultAddress(addressId);
    }

    //xóa địa chỉ
    @DeleteMapping("/{addressId}")
    public void deleteAddress(@PathVariable String addressId) {
        addressService.deleteAddress(addressId);
    }
}
