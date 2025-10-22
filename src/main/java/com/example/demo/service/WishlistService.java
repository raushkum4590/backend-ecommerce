package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.entity.Wishlist;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public List<Wishlist> getUserWishlist(User user) {
        return wishlistRepository.findByUser(user);
    }

    @Transactional
    public Wishlist addToWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (wishlistRepository.existsByUserAndProduct(user, product)) {
            throw new IllegalArgumentException("Product already in wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public void removeFromWishlist(Long wishlistId) {
        wishlistRepository.deleteById(wishlistId);
    }

    @Transactional
    public void removeProductFromWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        wishlistRepository.deleteByUserAndProduct(user, product);
    }

    public boolean isInWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return wishlistRepository.existsByUserAndProduct(user, product);
    }
}

