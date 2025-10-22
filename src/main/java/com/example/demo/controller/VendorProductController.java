package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.VendorService;
import com.example.demo.entity.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendor/products")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('VENDOR')")
public class VendorProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private VendorService vendorService;

    // Get my products
    @GetMapping
    public ResponseEntity<?> getMyProducts(Authentication authentication) {
        try {
            Vendor vendor = vendorService.getVendorByEmail(authentication.getName());
            List<Product> products = productService.getProductsByVendor(vendor.getId());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Add new product
    @PostMapping
    public ResponseEntity<?> addProduct(
            @RequestBody Product product,
            Authentication authentication) {
        try {
            Vendor vendor = vendorService.getVendorByEmail(authentication.getName());
            Product created = productService.createProductForVendor(vendor.getId(), product);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Product added successfully",
                "product", created
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Update my product
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long productId,
            @RequestBody Product product,
            Authentication authentication) {
        try {
            Vendor vendor = vendorService.getVendorByEmail(authentication.getName());
            Product updated = productService.updateProductForVendor(vendor.getId(), productId, product);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Product updated successfully",
                "product", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Delete my product
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long productId,
            Authentication authentication) {
        try {
            Vendor vendor = vendorService.getVendorByEmail(authentication.getName());
            productService.deleteProductForVendor(vendor.getId(), productId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Product deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get product statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getProductStats(Authentication authentication) {
        try {
            Vendor vendor = vendorService.getVendorByEmail(authentication.getName());
            List<Product> products = productService.getProductsByVendor(vendor.getId());

            long totalProducts = products.size();
            long availableProducts = products.stream().filter(Product::getIsAvailable).count();
            long outOfStock = products.stream().filter(p -> p.getStock() == 0).count();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalProducts", totalProducts);
            stats.put("availableProducts", availableProducts);
            stats.put("outOfStock", outOfStock);
            stats.put("inactiveProducts", totalProducts - availableProducts);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

