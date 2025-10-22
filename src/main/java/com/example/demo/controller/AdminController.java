package com.example.demo.controller;

import com.example.demo.dto.AdminProductRequest;
import com.example.demo.entity.*;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.VendorService;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {

    private final OrderService orderService;
    private final ProductService productService;
    private final UserRepository userRepository;
    private final VendorService vendorService;
    private final CategoryRepository categoryRepository;

    // Dashboard Statistics
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalRevenue", orderService.getTotalRevenue());
        stats.put("totalOrders", orderService.getAllOrders().size());
        stats.put("totalProducts", productService.getAllProducts().size());
        stats.put("totalUsers", userRepository.count());

        List<Order> pendingOrders = orderService.getOrdersByStatus(OrderStatus.PENDING);
        stats.put("pendingOrders", pendingOrders.size());

        return ResponseEntity.ok(stats);
    }

    // Order Management
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/status/{status}")
    public List<Order> getOrdersByStatus(@PathVariable OrderStatus status) {
        return orderService.getOrdersByStatus(status);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateRequest request) {
        try {
            Order order = orderService.updateOrderStatus(id, request.getStatus());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/orders/{id}/payment-status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody PaymentStatusUpdateRequest request) {
        try {
            Order order = orderService.updatePaymentStatus(id, request.getStatus());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Mark COD order as paid when cash is collected
    @PostMapping("/orders/{id}/mark-cod-paid")
    public ResponseEntity<?> markCodOrderAsPaid(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);

            // Verify it's a COD order
            if (!"cash".equalsIgnoreCase(order.getPaymentMethod()) &&
                    !"cod".equalsIgnoreCase(order.getPaymentMethod())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "This order is not a Cash on Delivery order"
                ));
            }

            // Mark as completed
            Order updatedOrder = orderService.updatePaymentStatus(id, PaymentStatus.COMPLETED);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "COD payment marked as completed",
                    "order", updatedOrder
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/orders/{id}/tracking")
    public ResponseEntity<?> updateTrackingNumber(
            @PathVariable Long id,
            @RequestBody TrackingUpdateRequest request) {
        try {
            Order order = orderService.updateTrackingNumber(id, request.getTrackingNumber());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // User Management
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // User statistics endpoint - MUST come before /users/{id}
    @GetMapping("/users/stats")
    public ResponseEntity<?> getUserStatistics() {
        List<User> allUsers = userRepository.findAll();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", allUsers.size());
        stats.put("activeUsers", allUsers.stream().filter(User::getIsActive).count());
        stats.put("inactiveUsers", allUsers.stream().filter(u -> !u.getIsActive()).count());
        stats.put("adminCount", allUsers.stream().filter(u -> u.getRole() == UserRole.ADMIN).count());
        stats.put("vendorCount", allUsers.stream().filter(u -> u.getRole() == UserRole.VENDOR).count());
        stats.put("customerCount", allUsers.stream().filter(u -> u.getRole() == UserRole.USER).count());

        return ResponseEntity.ok(stats);
    }

    // Search users - MUST come before /users/{id}
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        List<User> users = userRepository.findAll().stream()
                .filter(user ->
                        user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                                user.getUsername().toLowerCase().contains(query.toLowerCase())
                )
                .toList();

        return ResponseEntity.ok(users);
    }

    // Get user by email - MUST come before /users/{id}
    @GetMapping("/users/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    Map<String, Object> userDetails = new HashMap<>();
                    userDetails.put("id", user.getId());
                    userDetails.put("username", user.getUsername());
                    userDetails.put("email", user.getEmail());
                    userDetails.put("role", user.getRole().name());
                    userDetails.put("phoneNumber", user.getPhoneNumber());
                    userDetails.put("isActive", user.getIsActive());
                    userDetails.put("createdAt", user.getCreatedAt());
                    userDetails.put("lastLogin", user.getLastLogin());
                    return ResponseEntity.ok(userDetails);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all orders for a specific user by email - MUST come before /users/{id}
    @GetMapping("/users/email/{email}/orders")
    public ResponseEntity<?> getOrdersByUserEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    List<Order> orders = orderService.getOrdersByUser(user);

                    Map<String, Object> response = new HashMap<>();
                    response.put("user", Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "email", user.getEmail(),
                            "role", user.getRole().name()
                    ));
                    response.put("totalOrders", orders.size());
                    response.put("orders", orders);

                    // Calculate total spent
                    double totalSpent = orders.stream()
                            .mapToDouble(Order::getTotalAmount)
                            .sum();
                    response.put("totalSpent", totalSpent);

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(404).body(Map.of("error", "User not found with email: " + email)));
    }

    // Reset user password (admin can set new password)
    @PutMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetUserPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "New password is required"));
        }

        return userRepository.findById(id)
                .map(user -> {
                    // Password will be encoded by the service
                    user.setPassword(newPassword);
                    userRepository.save(user);
                    return ResponseEntity.ok(Map.of(
                            "message", "Password reset successfully for user: " + user.getEmail()
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Generic ID endpoint - MUST come AFTER all specific paths
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setIsActive(false);
                    userRepository.save(user);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setIsActive(true);
                    userRepository.save(user);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== VENDOR MANAGEMENT ====================

    @GetMapping("/vendors")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        List<Vendor> vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/vendors/pending")
    public ResponseEntity<List<Vendor>> getPendingVendors() {
        List<Vendor> vendors = vendorService.getAllVendors();
        List<Vendor> pending = vendors.stream()
                .filter(v -> !v.getIsVerified())
                .toList();
        return ResponseEntity.ok(pending);
    }

    @GetMapping("/vendors/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable Long id) {
        try {
            Vendor vendor = vendorService.getVendorById(id);
            return ResponseEntity.ok(vendor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/vendors/{id}/verify")
    public ResponseEntity<?> verifyVendor(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        try {
            Boolean verified = request.get("verified");
            Vendor vendor = vendorService.verifyVendor(id, verified);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", verified ? "Vendor verified successfully" : "Vendor verification revoked",
                    "vendor", vendor
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/vendors/{id}/status")
    public ResponseEntity<?> toggleVendorStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        try {
            Boolean active = request.get("active");
            Vendor vendor = vendorService.toggleVendorStatus(id, active);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", active ? "Vendor activated successfully" : "Vendor deactivated",
                    "vendor", vendor
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/vendors/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable Long id) {
        try {
            vendorService.toggleVendorStatus(id, false);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Vendor deactivated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== PRODUCT MANAGEMENT ====================

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/by-source")
    public ResponseEntity<?> getProductsBySource() {
        List<Product> allProducts = productService.getAllProducts();

        List<Product> adminProducts = allProducts.stream()
                .filter(p -> p.getVendor() == null)
                .toList();

        List<Product> vendorProducts = allProducts.stream()
                .filter(p -> p.getVendor() != null)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("totalProducts", allProducts.size());
        response.put("adminProducts", adminProducts.size());
        response.put("vendorProducts", vendorProducts.size());
        response.put("adminProductList", adminProducts);
        response.put("vendorProductList", vendorProducts);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("product", product);
            response.put("source", product.getVendor() != null ? "VENDOR" : "ADMIN");
            if (product.getVendor() != null) {
                response.put("vendorInfo", Map.of(
                        "id", product.getVendor().getId(),
                        "storeName", product.getVendor().getStoreName(),
                        "businessEmail", product.getVendor().getBusinessEmail()
                ));
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody AdminProductRequest request) {
        try {
            // Create product from request
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStock(request.getStock());
            product.setImageUrl(request.getImageUrl());
            product.setBrand(request.getBrand());
            product.setUnit(request.getUnit());
            product.setWeight(request.getWeight());
            product.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
            product.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
            product.setDiscount(request.getDiscount() != null ? request.getDiscount() : 0.0);

            // Set category if provided
            if (request.getCategoryId() != null) {
                Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                product.setCategory(category);
            }

            // Set vendor if provided (for admin adding vendor products)
            if (request.getVendorId() != null) {
                Vendor vendor = new Vendor();
                vendor.setId(request.getVendorId());
                product.setVendor(vendor);
            }

            Product created = productService.createProduct(product);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product created successfully",
                    "product", created
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to create product: " + e.getMessage(),
                            "data", null
                    ));
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        try {
            Product updated = productService.updateProduct(id, product);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product updated successfully",
                    "product", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== COMPREHENSIVE STATISTICS ====================

    @GetMapping("/stats/comprehensive")
    public ResponseEntity<Map<String, Object>> getComprehensiveStats() {
        Map<String, Object> stats = new HashMap<>();

        // User Statistics
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findAll().stream().filter(User::getIsActive).count();
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);

        // Vendor Statistics
        List<Vendor> allVendors = vendorService.getAllVendors();
        long totalVendors = allVendors.size();
        long verifiedVendors = allVendors.stream().filter(Vendor::getIsVerified).count();
        long pendingVendors = allVendors.stream().filter(v -> !v.getIsVerified()).count();
        stats.put("totalVendors", totalVendors);
        stats.put("verifiedVendors", verifiedVendors);
        stats.put("pendingVendors", pendingVendors);

        // Product Statistics
        List<Product> allProducts = productService.getAllProducts();
        long totalProducts = allProducts.size();
        long availableProducts = allProducts.stream().filter(Product::getIsAvailable).count();
        stats.put("totalProducts", totalProducts);
        stats.put("availableProducts", availableProducts);

        // Order Statistics
        List<Order> allOrders = orderService.getAllOrders();
        long totalOrders = allOrders.size();
        double totalRevenue = allOrders.stream()
                .filter(o -> o.getPaymentStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Order::getTotalAmount)
                .sum();
        stats.put("totalOrders", totalOrders);
        stats.put("totalRevenue", totalRevenue);

        // Order Status Breakdown
        Map<String, Long> ordersByStatus = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            long count = allOrders.stream().filter(o -> o.getOrderStatus() == status).count();
            ordersByStatus.put(status.name(), count);
        }
        stats.put("ordersByStatus", ordersByStatus);

        return ResponseEntity.ok(stats);
    }
}
