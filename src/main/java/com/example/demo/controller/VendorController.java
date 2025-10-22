package com.example.demo.controller;

import com.example.demo.dto.VendorRegistrationRequest;
import com.example.demo.dto.VendorUpdateRequest;
import com.example.demo.entity.Vendor;
import com.example.demo.service.VendorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendor")
@CrossOrigin(origins = "*")
public class VendorController {

    private static final Logger logger = LoggerFactory.getLogger(VendorController.class);

    @Autowired
    private VendorService vendorService;

    // Register as vendor (authenticated user) - supports both JSON and multipart
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> registerVendor(
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String businessEmail,
            @RequestParam(required = false) String businessPhone,
            @RequestParam(required = false) String businessAddress,
            @RequestParam(required = false) String businessLicense,
            @RequestParam(required = false) String taxId,
            @RequestParam(required = false) String logoUrl,
            @RequestParam(required = false) String bannerUrl,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "banner", required = false) MultipartFile banner,
            Authentication authentication) {
        try {
            logger.info("Vendor registration attempt by user: {}", authentication.getName());
            logger.info("Request data: storeName={}, businessEmail={}", storeName, businessEmail);

            // Validate required fields
            if (storeName == null || storeName.trim().isEmpty()) {
                logger.warn("Registration failed: Store name is required");
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Store name is required"
                ));
            }

            if (businessEmail == null || businessEmail.trim().isEmpty()) {
                logger.warn("Registration failed: Business email is required");
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Business email is required"
                ));
            }

            if (businessPhone == null || businessPhone.trim().isEmpty()) {
                logger.warn("Registration failed: Business phone is required");
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Business phone is required"
                ));
            }

            // Log file uploads if present
            if (logo != null && !logo.isEmpty()) {
                logger.info("Logo file uploaded: {} ({})", logo.getOriginalFilename(), logo.getSize());
            }
            if (banner != null && !banner.isEmpty()) {
                logger.info("Banner file uploaded: {} ({})", banner.getOriginalFilename(), banner.getSize());
            }

            Vendor vendor = new Vendor();
            vendor.setStoreName(storeName);
            vendor.setDescription(description);
            vendor.setBusinessLicense(businessLicense);
            vendor.setTaxId(taxId);
            vendor.setBusinessEmail(businessEmail);
            vendor.setBusinessPhone(businessPhone);
            vendor.setBusinessAddress(businessAddress);
            vendor.setLogoUrl(logoUrl);
            vendor.setBannerUrl(bannerUrl);

            Vendor created = vendorService.createVendor(authentication.getName(), vendor);

            logger.info("Vendor registration successful for user: {}, vendorId: {}",
                authentication.getName(), created.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Vendor registration successful! Awaiting admin verification.");
            response.put("vendor", created);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Vendor registration failed for user: {}", authentication.getName(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("error", e.getClass().getSimpleName());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            logger.error("Unexpected error during vendor registration for user: {}",
                authentication.getName(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "An unexpected error occurred: " + e.getMessage());
            error.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Get my vendor profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            Vendor vendor = vendorService.getVendorByEmail(authentication.getName());
            return ResponseEntity.ok(vendor);
        } catch (Exception e) {
            logger.error("Error fetching vendor profile for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Update vendor profile
    @PutMapping("/profile")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> updateProfile(
            @RequestBody VendorUpdateRequest request,
            Authentication authentication) {
        try {
            Vendor updateData = new Vendor();
            updateData.setStoreName(request.getStoreName());
            updateData.setDescription(request.getDescription());
            updateData.setBusinessEmail(request.getBusinessEmail());
            updateData.setBusinessPhone(request.getBusinessPhone());
            updateData.setBusinessAddress(request.getBusinessAddress());
            updateData.setLogoUrl(request.getLogoUrl());
            updateData.setBannerUrl(request.getBannerUrl());

            Vendor updated = vendorService.updateVendor(authentication.getName(), updateData);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile updated successfully",
                "vendor", updated
            ));
        } catch (Exception e) {
            logger.error("Error updating vendor profile for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get vendor by ID (public)
    @GetMapping("/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable Long id) {
        try {
            Vendor vendor = vendorService.getVendorById(id);
            return ResponseEntity.ok(vendor);
        } catch (Exception e) {
            logger.error("Error fetching vendor by ID: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get all vendors (public - only active and verified)
    @GetMapping("/all")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        List<Vendor> vendors = vendorService.getVerifiedVendors();
        return ResponseEntity.ok(vendors);
    }

    // Delete vendor account (vendor can deactivate their own account)
    @DeleteMapping("/profile")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> deleteMyVendorAccount(Authentication authentication) {
        try {
            vendorService.deleteVendor(authentication.getName());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vendor account deactivated successfully"
            ));
        } catch (Exception e) {
            logger.error("Error deactivating vendor account for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
