package com.example.demo.service;

import com.example.demo.dto.CartSummary;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<CartItem> getCartItems(User user) {
        return cartRepository.findByUser(user);
    }

    @Transactional
    public CartItem addToCart(User user, Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<CartItem> userCart = cartRepository.findByUser(user);
        CartItem existingItem = userCart.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartRepository.save(existingItem);
        }

        CartItem newItem = new CartItem();
        newItem.setUser(user);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        return cartRepository.save(newItem);
    }

    @Transactional
    public CartItem updateCartItemQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    @Transactional
    public void removeFromCart(Long cartItemId) {
        cartRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart(User user) {
        cartRepository.deleteByUser(user);
    }

    @Transactional
    public CartItem incrementQuantity(Long cartItemId) {
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItem.setQuantity(cartItem.getQuantity() + 1);
        return cartRepository.save(cartItem);
    }

    @Transactional
    public CartItem decrementQuantity(Long cartItemId) {
        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (cartItem.getQuantity() <= 1) {
            throw new RuntimeException("Cannot decrease quantity below 1");
        }

        cartItem.setQuantity(cartItem.getQuantity() - 1);
        return cartRepository.save(cartItem);
    }

    public CartSummary getCartSummary(User user) {
        List<CartItem> cartItems = getCartItems(user);

        int totalItems = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        double subtotal = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        double shippingFee = subtotal >= 50.0 ? 0.0 : 5.0;
        double tax = subtotal * 0.08;
        double total = subtotal + shippingFee + tax;

        return new CartSummary(totalItems, subtotal, shippingFee, tax, total);
    }
}
