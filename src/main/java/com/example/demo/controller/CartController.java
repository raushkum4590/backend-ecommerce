package com.example.demo.controller;

import com.example.demo.dto.CartSummary;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public List<CartItem> getCart() {
        return cartService.getCartItems(getCurrentUser());
    }

    @GetMapping("/summary")
    public ResponseEntity<CartSummary> getCartSummary() {
        return ResponseEntity.ok(cartService.getCartSummary(getCurrentUser()));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest request) {
        CartItem cartItem = cartService.addToCart(
            getCurrentUser(),
            request.getProductId(),
            request.getQuantity()
        );
        return ResponseEntity.ok(cartItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuantity(
            @PathVariable Long id,
            @RequestBody CartRequest request) {
        CartItem cartItem = cartService.updateCartItemQuantity(id, request.getQuantity());
        return ResponseEntity.ok(cartItem);
    }

    @PutMapping("/{id}/increment")
    public ResponseEntity<?> incrementQuantity(@PathVariable Long id) {
        CartItem cartItem = cartService.incrementQuantity(id);
        return ResponseEntity.ok(cartItem);
    }

    @PutMapping("/{id}/decrement")
    public ResponseEntity<?> decrementQuantity(@PathVariable Long id) {
        CartItem cartItem = cartService.decrementQuantity(id);
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {
        cartService.clearCart(getCurrentUser());
        return ResponseEntity.ok().build();
    }
}
