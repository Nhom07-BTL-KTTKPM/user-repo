package iuh.fit.userservice.customer.service;

import iuh.fit.userservice.customer.entity.Address;
import iuh.fit.userservice.customer.repository.AddressRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address getAddressById(String addressId) {
        return addressRepository.findById(UUID.fromString(addressId))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ với id: " + addressId));
    }

    public Address getDefaultAddressByCustomerId(String customerId) {
        return addressRepository.findByCustomerId(customerId).stream()
                .filter(Address::getIsDefault)
                .findFirst()
                .orElse(null);
    }

    public List<Address> getAddressByCustomerId(String customerId) {
        return addressRepository.findByCustomerId(customerId);
    }

    public void addAddress(String customerId, Address address) {
        //nếu chưa có địa chỉ nào thì mặc định địa chỉ đầu tiên là mặc định
        if (addressRepository.findByCustomerId(customerId).isEmpty()) {
            address.setDefault(true);
        } else {
            address.setDefault(false);
        }
        address.setCustomerId(customerId);
        addressRepository.save(address);
    }

    public void setDefaultAddress(String addressId) {
        String customerId = addressRepository.findById(UUID.fromString(addressId))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ với id: " + addressId))
                .getCustomerId();
        addressRepository.findByCustomerId(customerId).forEach(addr -> {
            if (addr.getId().toString().equals(addressId)) {
                addr.setDefault(true);
            } else {
                addr.setDefault(false);
            }
            addressRepository.save(addr);
        });
    }

    public void updateAddress(String addressId, Address updatedAddress) {
        Address existingAddress = addressRepository.findById(UUID.fromString(addressId))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ với id: " + addressId));

        existingAddress.setRecipientName(updatedAddress.getRecipientName());
        existingAddress.setPhone(updatedAddress.getPhone());
        existingAddress.setStreetAddress(updatedAddress.getStreetAddress());
        existingAddress.setWard(updatedAddress.getWard());
        existingAddress.setDistrict(updatedAddress.getDistrict());
        existingAddress.setCity(updatedAddress.getCity());

        addressRepository.save(existingAddress);
    }

    public void deleteAddress(String addressId) {
        Address address = addressRepository.findById(UUID.fromString(addressId))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ với id: " + addressId));
        Boolean wasDefault = address.getIsDefault();
        if(wasDefault) {
            throw new RuntimeException("Không thể xóa địa chỉ mặc định. Vui lòng đặt một địa chỉ khác làm mặc định trước khi xóa.");
        } else {
            addressRepository.delete(address);
        }
    }
}
