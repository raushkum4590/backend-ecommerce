package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.Wishlist;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public List<Wishlist> getWishlist() {
        return wishlistService.getUserWishlist(getCurrentUser());
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addToWishlist(@PathVariable Long productId) {
        try {
            Wishlist wishlist = wishlistService.addToWishlist(getCurrentUser(), productId);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long id) {
        wishlistService.removeFromWishlist(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> removeProductFromWishlist(@PathVariable Long productId) {
        try {
            wishlistService.removeProductFromWishlist(getCurrentUser(), productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkWishlist(@PathVariable Long productId) {
        try {
            boolean inWishlist = wishlistService.isInWishlist(getCurrentUser(), productId);
            return ResponseEntity.ok(Map.of("inWishlist", inWishlist));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

