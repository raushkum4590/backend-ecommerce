package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "false")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        try {
            Order order = orderService.createOrder(
                getCurrentUser(),
                request.getAddressId(),
                request.getPaymentMethod()
            );
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<Order> getUserOrders() {
        return orderService.getUserOrders(getCurrentUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            // Only allow users to view their own orders
            if (!order.getUser().getId().equals(getCurrentUser().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, @RequestBody(required = false) CancelRequest request) {
        try {
            Order order = orderService.getOrderById(id);
            if (!order.getUser().getId().equals(getCurrentUser().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            String reason = request != null ? request.getReason() : "Customer requested cancellation";
            Order cancelledOrder = orderService.cancelOrder(id, reason);
            return ResponseEntity.ok(cancelledOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
