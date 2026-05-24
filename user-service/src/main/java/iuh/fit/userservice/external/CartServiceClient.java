package iuh.fit.userservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

@FeignClient(name = "cart-service")
public interface CartServiceClient {

    @PostMapping("/api/v1/carts")
    ResponseEntity<Object> createCart(@RequestBody CreateCartRequest request);

    record CreateCartRequest(String customerId) {}
}
