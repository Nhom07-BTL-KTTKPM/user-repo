package iuh.fit.userservice.customer.controller;

import iuh.fit.shared.api.ApiResponse;
import iuh.fit.shared.trace.TraceIdContext;
import iuh.fit.userservice.customer.entity.WishItem;
import iuh.fit.userservice.customer.service.WishItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/customers")
public class WishItemController {

    private final WishItemService wishItemService;

    public WishItemController(WishItemService wishItemService) {
        this.wishItemService = wishItemService;
    }

    @PostMapping("/{customerId}/wishlist/{productId}")
    public ResponseEntity<ApiResponse<WishItem>> addToWishlist(
            @PathVariable String customerId,
            @PathVariable String productId) {

        WishItem wishItem = wishItemService.addToWishlist(customerId, productId);
        return ResponseEntity.ok(
                ApiResponse.success(wishItem, "Thêm sản phẩm yêu thích thành công", resolveTraceId())
        );
    }

    @GetMapping("/{customerId}/wishlist")
    public ResponseEntity<ApiResponse<List<WishItem>>> getWishlist(
            @PathVariable String customerId) {

        List<WishItem> wishlist = wishItemService.getWishlist(customerId);
        return ResponseEntity.ok(
                ApiResponse.success(wishlist, "Lấy danh sách yêu thích thành công", resolveTraceId())
        );
    }

    @DeleteMapping("/{customerId}/wishlist/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @PathVariable String customerId,
            @PathVariable String productId) {

        wishItemService.removeFromWishlist(customerId, productId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Xóa sản phẩm yêu thích thành công", resolveTraceId())
        );
    }

    private String resolveTraceId() {
        return TraceIdContext.get();
    }
}