package iuh.fit.userservice.customer.service;

import iuh.fit.shared.error.BusinessException;
import iuh.fit.shared.error.ErrorCode;
import iuh.fit.userservice.customer.entity.WishItem;
import iuh.fit.userservice.customer.repository.CustomerRepository;
import iuh.fit.userservice.customer.repository.WishItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WishItemService {

    private final WishItemRepository wishItemRepository;
    private final CustomerRepository customerRepository;

    public WishItemService(WishItemRepository wishItemRepository,
                           CustomerRepository customerRepository) {
        this.wishItemRepository = wishItemRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public WishItem addToWishlist(String customerId, String productId) {
        UUID customerUuid = parseUuid(customerId, "customerId");
        UUID productUuid = parseUuid(productId, "productId");

        customerRepository.findById(customerUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy khách hàng với id: " + customerId));

        Optional<WishItem> existing = wishItemRepository.findByCustomerIdAndProductId(customerUuid, productUuid);
        if (existing.isPresent()) {
            return existing.get();
        }

        WishItem wishItem = WishItem.builder()
                .customerId(customerUuid)
                .productId(productUuid)
                .build();

        return wishItemRepository.save(wishItem);
    }

    @Transactional
    public void removeFromWishlist(String customerId, String productId) {
        UUID customerUuid = parseUuid(customerId, "customerId");
        UUID productUuid = parseUuid(productId, "productId");

        customerRepository.findById(customerUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy khách hàng với id: " + customerId));

        WishItem wishItem = wishItemRepository.findByCustomerIdAndProductId(customerUuid, productUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy sản phẩm yêu thích với productId: " + productId));

        wishItemRepository.delete(wishItem);
    }

    public List<WishItem> getWishlist(String customerId) {
        UUID customerUuid = parseUuid(customerId, "customerId");

        customerRepository.findById(customerUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy khách hàng với id: " + customerId));

        return wishItemRepository.findByCustomerId(customerUuid);
    }

    private UUID parseUuid(String value, String fieldName) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,
                    fieldName + " không hợp lệ: " + value);
        }
    }
}