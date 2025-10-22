package com.example.demo.controller;

import com.example.demo.dto.PaymentConfirmRequest;
import com.example.demo.dto.PaymentIntentRequest;
import com.example.demo.dto.PaymentIntentResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.example.demo.service.PayPalPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "false")
public class PaymentController {

    private final PayPalPaymentService payPalPaymentService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Create a PayPal order for payment
     * POST /api/payments/create-payment
     */
    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody PaymentIntentRequest request) {
        try {
            // Validate request
            if (request.getOrderId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Order ID is required"));
            }

            if (request.getReturnUrl() == null || request.getCancelUrl() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Return URL and Cancel URL are required"));
            }

            // Get the order and verify it belongs to the current user
            Order order = orderService.getOrderById(request.getOrderId());

            if (!order.getUser().getId().equals(getCurrentUser().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You don't have permission to pay for this order"));
            }

            // Create PayPal order
            PaymentIntentResponse response = payPalPaymentService.createPayPalOrder(
                    order,
                    request.getCurrency(),
                    request.getReturnUrl(),
                    request.getCancelUrl()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("details", "Failed to create PayPal payment");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred");
            error.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Alias endpoint for backward compatibility
     * POST /api/payments/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPaymentAlias(@RequestBody PaymentIntentRequest request) {
        return createPayment(request);
    }

    /**
     * Capture payment after PayPal approval
     * POST /api/payments/capture
     */
    @PostMapping("/capture")
    public ResponseEntity<?> capturePayment(@RequestBody PaymentConfirmRequest request) {
        try {
            // Validate request
            if (request.getOrderId() == null || request.getPaypalOrderId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Order ID and PayPal Order ID are required"));
            }

            // Verify the order belongs to the current user
            Order order = orderService.getOrderById(request.getOrderId());

            if (!order.getUser().getId().equals(getCurrentUser().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You don't have permission to confirm payment for this order"));
            }

            // Capture payment with PayPal and update order
            Order updatedOrder = payPalPaymentService.confirmPayment(
                    request.getOrderId(),
                    request.getPaypalOrderId()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment captured successfully");
            response.put("order", updatedOrder);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("details", "Failed to capture PayPal payment");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred");
            error.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get payment status
     * GET /api/payments/status/{paypalOrderId}
     */
    @GetMapping("/status/{paypalOrderId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String paypalOrderId) {
        try {
            String status = payPalPaymentService.getPaymentStatus(paypalOrderId);

            Map<String, String> response = new HashMap<>();
            response.put("paypalOrderId", paypalOrderId);
            response.put("status", status);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
