package com.example.demo.controller;

import com.example.demo.dto.PaymentConfirmRequest;
import com.example.demo.dto.PaymentIntentRequest;
import com.example.demo.dto.SimplePaymentRequest;
import com.example.demo.dto.ShippingAddressDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.entity.Address;
import com.example.demo.service.OrderService;
import com.example.demo.dto.PaymentIntentResponse;
import com.example.demo.service.PayPalPaymentService;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.AddressRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * Backward compatibility controller for /api/payment endpoints
 * Redirects to the main PaymentController (/api/payments)
 */
@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "false")
public class PaymentAliasController {

    private final PaymentController paymentController;
    private final OrderService orderService;
    private final PayPalPaymentService payPalPaymentService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Getting current user from authentication: {}", auth.getName());
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * SIMPLIFIED ENDPOINT: Creates order from cart and initiates PayPal payment in ONE step
     * POST /api/payment/create
     *
     * Request body with shipping address:
     * {
     *   "shippingAddress": {
     *     "street": "123 Main St",
     *     "city": "New York",
     *     "state": "NY",
     *     "zipCode": "10001",
     *     "country": "USA",
     *     "phoneNumber": "555-1234"
     *   },
     *   "currency": "USD",
     *   "returnUrl": "http://localhost:3000/payment/success",
     *   "cancelUrl": "http://localhost:3000/payment/cancel"
     * }
     *
     * OR pass empty body to use first saved address:
     * {}
     */
    @PostMapping("/create")
    public ResponseEntity<?> createDirectPayment(@RequestBody(required = false) SimplePaymentRequest request) {
        try {
            log.info("=== PAYMENT CREATE REQUEST STARTED ===");

            User user = getCurrentUser();
            log.info("User authenticated: {} (ID: {})", user.getEmail(), user.getId());

            // Handle null request
            if (request == null) {
                log.info("Received null request body, creating empty request object");
                request = new SimplePaymentRequest();
            }

            Long addressId;

            // Check if shipping address is provided in request
            if (request.getShippingAddress() != null) {
                log.info("Shipping address provided in request, validating...");
                ShippingAddressDTO shippingDto = request.getShippingAddress();

                log.info("Received shipping address: street={}, city={}, state={}, zipCode={}, country={}, phone={}",
                    shippingDto.getStreet(),
                    shippingDto.getCity(),
                    shippingDto.getState(),
                    shippingDto.getZipCode(),
                    shippingDto.getCountry(),
                    shippingDto.getPhoneNumber());

                // Validate shipping address with detailed logging
                if (shippingDto.getStreet() == null || shippingDto.getStreet().trim().isEmpty()) {
                    log.error("Validation failed: Street address is missing or empty");
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Street address is required",
                                     "field", "street",
                                     "details", "Please provide a valid street address"));
                }

                if (shippingDto.getCity() == null || shippingDto.getCity().trim().isEmpty()) {
                    log.error("Validation failed: City is missing or empty");
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "City is required",
                                     "field", "city",
                                     "details", "Please provide a valid city"));
                }

                if (shippingDto.getState() == null || shippingDto.getState().trim().isEmpty()) {
                    log.error("Validation failed: State is missing or empty");
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "State is required",
                                     "field", "state",
                                     "details", "Please provide a valid state"));
                }

                if (shippingDto.getZipCode() == null || shippingDto.getZipCode().trim().isEmpty()) {
                    log.error("Validation failed: Zip code is missing or empty");
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Zip code is required",
                                     "field", "zipCode",
                                     "details", "Please provide a valid zip code"));
                }

                log.info("Shipping address validation passed, creating address entity...");

                // Create and save address
                Address address = new Address();
                address.setUser(user);
                address.setAddressLine1(shippingDto.getStreet()); // Use addressLine1 instead of street
                address.setCity(shippingDto.getCity());
                address.setState(shippingDto.getState());
                address.setPostalCode(shippingDto.getZipCode()); // Use postalCode instead of zipCode
                address.setCountry(shippingDto.getCountry() != null ? shippingDto.getCountry() : "USA");
                address.setPhoneNumber(shippingDto.getPhoneNumber());

                Address savedAddress = addressRepository.save(address);
                addressId = savedAddress.getId();
                log.info("Address saved successfully with ID: {}", addressId);
            } else {
                log.info("No shipping address in request, attempting to use saved address...");
                addressId = getFirstUserAddressId(user);
                log.info("Using existing address ID: {}", addressId);
            }

            String currency = request.getCurrency() != null ? request.getCurrency() : "USD";
            String returnUrl = request.getReturnUrl() != null
                ? request.getReturnUrl()
                : "http://localhost:3000/payment/success";
            String cancelUrl = request.getCancelUrl() != null
                ? request.getCancelUrl()
                : "http://localhost:3000/payment/cancel";

            log.info("Payment parameters - Currency: {}, ReturnUrl: {}, CancelUrl: {}",
                currency, returnUrl, cancelUrl);

            // Step 1: Create order from user's cart
            log.info("Step 1: Creating order from user's cart...");
            Order order = orderService.createOrder(user, addressId, "PAYPAL");
            log.info("Order created successfully - Order ID: {}, Total: ${}",
                order.getId(), order.getTotalAmount());

            // Step 2: Create PayPal payment for this order
            log.info("Step 2: Creating PayPal payment order...");
            PaymentIntentResponse paymentResponse = payPalPaymentService.createPayPalOrder(
                order,
                currency,
                returnUrl,
                cancelUrl
            );
            log.info("PayPal order created - PayPal Order ID: {}, Status: {}",
                paymentResponse.getPaypalOrderId(), paymentResponse.getStatus());

            // Step 3: Return combined response
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("orderTotal", order.getTotalAmount());
            response.put("approvalUrl", paymentResponse.getApprovalUrl());
            response.put("paypalOrderId", paymentResponse.getPaypalOrderId());
            response.put("currency", paymentResponse.getCurrency());
            response.put("status", paymentResponse.getStatus());
            response.put("message", "Order created and PayPal payment initiated successfully");

            log.info("=== PAYMENT CREATE REQUEST COMPLETED SUCCESSFULLY ===");
            log.info("Response: orderId={}, paypalOrderId={}, approvalUrl={}",
                order.getId(), paymentResponse.getPaypalOrderId(), paymentResponse.getApprovalUrl());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("=== PAYMENT CREATE REQUEST FAILED ===");
            log.error("Error type: RuntimeException");
            log.error("Error message: {}", e.getMessage());
            log.error("Stack trace: ", e);

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("details", "Failed to create order and payment. Make sure you have items in your cart and provide a valid shipping address.");
            error.put("type", "RuntimeException");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            log.error("=== PAYMENT CREATE REQUEST FAILED ===");
            log.error("Error type: {}", e.getClass().getName());
            log.error("Error message: {}", e.getMessage());
            log.error("Stack trace: ", e);

            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred");
            error.put("details", e.getMessage());
            error.put("type", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Test endpoint to verify controller is accessible
     * GET /api/payment/test
     */
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Payment controller is accessible");
        response.put("timestamp", System.currentTimeMillis());
        log.info("Test endpoint called - controller is working");
        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint with authentication
     * GET /api/payment/test-auth
     */
    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuthEndpoint() {
        try {
            User user = getCurrentUser();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "OK");
            response.put("message", "Authentication working");
            response.put("user", user.getEmail());
            response.put("userId", user.getId());
            log.info("Test auth endpoint called - user: {}", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Test auth endpoint failed: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Helper method to get user's first address
     */
    private Long getFirstUserAddressId(User user) {
        log.info("Attempting to get first address for user ID: {}", user.getId());
        var addresses = user.getAddresses();

        if (addresses != null && !addresses.isEmpty()) {
            Long firstAddressId = addresses.get(0).getId();
            log.info("Found {} saved addresses, using first address ID: {}",
                addresses.size(), firstAddressId);
            return firstAddressId;
        }

        log.error("No saved addresses found for user ID: {}", user.getId());
        throw new RuntimeException("No shipping address found. Please provide shipping address in the request.");
    }

    /**
     * Original alias endpoint - requires orderId
     * POST /api/payment/create-with-order
     */
    @PostMapping("/create-with-order")
    public ResponseEntity<?> create(@RequestBody PaymentIntentRequest request) {
        return paymentController.createPayment(request);
    }

    /**
     * Alias for POST /api/payments/capture
     * POST /api/payment/capture
     */
    @PostMapping("/capture")
    public ResponseEntity<?> capture(@RequestBody PaymentConfirmRequest request) {
        return paymentController.capturePayment(request);
    }

    /**
     * Alias for GET /api/payments/status/{paypalOrderId}
     * GET /api/payment/status/{paypalOrderId}
     */
    @GetMapping("/status/{paypalOrderId}")
    public ResponseEntity<?> getStatus(@PathVariable String paypalOrderId) {
        return paymentController.getPaymentStatus(paypalOrderId);
    }
}
