package iuh.fit.userservice.customer.service;

import iuh.fit.shared.error.BusinessException;
import iuh.fit.shared.error.ErrorCode;
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
        UUID addressUuid = parseAddressId(addressId);
        return addressRepository.findById(addressUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy địa chỉ với id: " + addressId));
    }

    public Address getDefaultAddressByCustomerId(String customerId) {
        return addressRepository.findByCustomerId(customerId).stream()
                .filter(Address::getIsDefault)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy địa chỉ mặc định của khách hàng"));
    }

    public List<Address> getAddressByCustomerId(String customerId) {
        return addressRepository.findByCustomerId(customerId);
    }

    public void addAddress(String customerId, Address address) {
        try {
            if (addressRepository.findByCustomerId(customerId).isEmpty()) {
                address.setDefault(true);
            } else {
                address.setDefault(false);
            }
            address.setCustomerId(customerId);
            addressRepository.save(address);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Lỗi khi thêm địa chỉ mới");
        }
    }

    public void setDefaultAddress(String addressId) {
        UUID addressUuid = parseAddressId(addressId);
        Address selectedAddress = addressRepository.findById(addressUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy địa chỉ với id: " + addressId));
        try {
            String customerId = selectedAddress.getCustomerId();
            addressRepository.findByCustomerId(customerId).forEach(addr -> {
                if (addr.getId().toString().equals(addressId)) {
                    addr.setDefault(true);
                } else {
                    addr.setDefault(false);
                }
                addressRepository.save(addr);
            });
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Lỗi khi đặt địa chỉ mặc định");
        }
    }

    public void updateAddress(String addressId, Address updatedAddress) {
        UUID addressUuid = parseAddressId(addressId);
        Address existingAddress = addressRepository.findById(addressUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy địa chỉ với id: " + addressId));

        try {
            existingAddress.setRecipientName(updatedAddress.getRecipientName());
            existingAddress.setPhone(updatedAddress.getPhone());
            existingAddress.setStreetAddress(updatedAddress.getStreetAddress());
            existingAddress.setWard(updatedAddress.getWard());
            existingAddress.setDistrict(updatedAddress.getDistrict());
            existingAddress.setCity(updatedAddress.getCity());
            addressRepository.save(existingAddress);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật địa chỉ");
        }
    }

    public void deleteAddress(String addressId) {
        UUID addressUuid = parseAddressId(addressId);
        Address address = addressRepository.findById(addressUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy địa chỉ với id: " + addressId));
        try {
            Boolean wasDefault = address.getIsDefault();
            if (wasDefault) {
                throw new BusinessException(
                        ErrorCode.CONFLICT,
                        "Không thể xóa địa chỉ mặc định. Vui lòng đặt một địa chỉ khác làm mặc định trước khi xóa."
                );
            }
            addressRepository.delete(address);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Lỗi khi xóa địa chỉ");
        }
    }

    private UUID parseAddressId(String addressId) {
        try {
            return UUID.fromString(addressId);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "addressId không hợp lệ");
        }
    }
}
