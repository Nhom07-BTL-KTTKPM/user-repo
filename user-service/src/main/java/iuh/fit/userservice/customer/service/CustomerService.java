package iuh.fit.userservice.customer.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import iuh.fit.shared.error.BusinessException;
import iuh.fit.shared.error.ErrorCode;
import iuh.fit.userservice.customer.dto.CustomerUpdateRequest;
import iuh.fit.userservice.customer.entity.Customer;
import iuh.fit.userservice.customer.repository.CustomerRepository;
import jakarta.transaction.Transactional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomerById(String customerId) {
        UUID customerUuid = parseCustomerId(customerId);
        return customerRepository.findById(customerUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy khách hàng"));
    }

    public Customer getCustomerByAccountId(String accountId) {
        return customerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy khách hàng"));
    }

    public void updateCustomerInfo(String customerId, CustomerUpdateRequest request) {
        UUID customerUuid = parseCustomerId(customerId);
        Customer customer = customerRepository.findById(customerUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy khách hàng với id: " + customerId));

        try {
            if (request.getFullName() != null) {
                customer.setFullName(request.getFullName());
            }
            if (request.getPhoneNumber() != null) {
                customer.setPhoneNumber(request.getPhoneNumber());
            }
            if (request.getDateOfBirth() != null) {
                customer.setDateOfBirth(request.getDateOfBirth());
            }
            if (request.getGender() != null) {
                customer.setGender(request.getGender());
            }
            if (request.getSkinType() != null) {
                customer.setSkinType(request.getSkinType());
            }
            customerRepository.save(customer);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật thông tin khách hàng");
        }
    }

    @Transactional
    public void updateLoyaltyPoints(String customerId, int points) {
        UUID customerUuid = parseCustomerId(customerId);
        Customer customer = customerRepository.findById(customerUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy khách hàng với id: " + customerId));

        try {
            int newPoints = customer.getLoyaltyPoints() + points;
            customer.setLoyaltyPoints(newPoints);
            customerRepository.save(customer);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật điểm tích lũy khách hàng");
        }
    }

    public void addSkinConcern(String customerId, String skinConcern) {
        UUID customerUuid = parseCustomerId(customerId);
        Customer customer = customerRepository.findById(customerUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy khách hàng với id: " + customerId));

        try {
            List<String> skinConcerns = customer.getSkinConcerns();
            if (!skinConcerns.contains(skinConcern)) {
                skinConcerns.add(skinConcern);
                customer.setSkinConcerns(skinConcerns);
                customerRepository.save(customer);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật vấn đề da của khách hàng");
        }
    }

    private UUID parseCustomerId(String customerId) {
        try {
            return UUID.fromString(customerId);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "customerId không hợp lệ");
        }
    }
}