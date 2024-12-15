package com.lcaohoanq.nocket.domain.cart;

import com.lcaohoanq.nocket.api.ApiResponse;
import com.lcaohoanq.nocket.api.PageResponse;
import com.lcaohoanq.nocket.domain.user.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/carts")
@Slf4j
@RequiredArgsConstructor
public class CartController {

    private final IUserService userService;
    private final ICartService cartService;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<PageResponse<CartResponse>> getAll(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(cartService.getAllCarts(PageRequest.of(page, limit)));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<CartResponse>> getCartById(
        @PathVariable(value = "id") Long id
    ) {
        return ResponseEntity.ok(
            ApiResponse.<CartResponse>builder()
                .message("Get cart by id successfully")
                .data(cartService.findById(id))
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .build()
        );
    }
    
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<CartResponse>> getCartByUserId(
        @RequestParam(value = "id") Long id
    ){
        return ResponseEntity.ok(
            ApiResponse.<CartResponse>builder()
                .data(cartService.getCartByUserId(id))
                .build()
        );
    }
    
    @PostMapping("/buy-products")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<ApiResponse<CartResponse>> buyProducts(
    ) {
       return ResponseEntity.ok(
            ApiResponse.<CartResponse>builder()
                .message("Buy products successfully")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .build()
        );
    }

    @PutMapping("/update-purchase")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<ApiResponse<CartResponse>> updatePurchases(
    ) {
        return ResponseEntity.ok(
            ApiResponse.<CartResponse>builder()
                .message("Update purchases successfully")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .build()
        );
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<ApiResponse<CartResponse>> deleteCart(
        @PathVariable(value = "id") Long id
    ) {
//        cartService.deleteCart(id);
        return ResponseEntity.ok(
            ApiResponse.<CartResponse>builder()
                .message("Delete cart successfully")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .build()
        );
    }

}
