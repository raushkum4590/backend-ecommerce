package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.entity.Vendor;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UserRepository userRepository;

    // Create vendor profile
    @Transactional
    public Vendor createVendor(String email, Vendor vendorData) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is already a vendor
        if (vendorRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("User already has a vendor profile");
        }

        // Check if store name is taken
        if (vendorRepository.findByStoreName(vendorData.getStoreName()).isPresent()) {
            throw new RuntimeException("Store name already taken");
        }

        // Update user role to VENDOR
        user.setRole(UserRole.VENDOR);
        userRepository.save(user);

        vendorData.setUser(user);
        vendorData.setIsVerified(false); // Admin must verify
        vendorData.setIsActive(true);

        return vendorRepository.save(vendorData);
    }

    // Get vendor by user email
    public Vendor getVendorByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return vendorRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Vendor profile not found"));
    }

    // Get vendor by ID
    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Vendor not found"));
    }

    // Update vendor profile
    @Transactional
    public Vendor updateVendor(String email, Vendor updatedData) {
        Vendor vendor = getVendorByEmail(email);

        if (updatedData.getStoreName() != null &&
            !updatedData.getStoreName().equals(vendor.getStoreName())) {
            if (vendorRepository.findByStoreName(updatedData.getStoreName()).isPresent()) {
                throw new RuntimeException("Store name already taken");
            }
            vendor.setStoreName(updatedData.getStoreName());
        }

        if (updatedData.getDescription() != null) {
            vendor.setDescription(updatedData.getDescription());
        }
        if (updatedData.getLogoUrl() != null) {
            vendor.setLogoUrl(updatedData.getLogoUrl());
        }
        if (updatedData.getBannerUrl() != null) {
            vendor.setBannerUrl(updatedData.getBannerUrl());
        }
        if (updatedData.getBusinessEmail() != null) {
            vendor.setBusinessEmail(updatedData.getBusinessEmail());
        }
        if (updatedData.getBusinessPhone() != null) {
            vendor.setBusinessPhone(updatedData.getBusinessPhone());
        }
        if (updatedData.getBusinessAddress() != null) {
            vendor.setBusinessAddress(updatedData.getBusinessAddress());
        }

        return vendorRepository.save(vendor);
    }

    // Get all vendors
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    // Get verified vendors
    public List<Vendor> getVerifiedVendors() {
        return vendorRepository.findByIsVerifiedTrue();
    }

    // Get active vendors
    public List<Vendor> getActiveVendors() {
        return vendorRepository.findByIsActiveTrue();
    }

    // Admin: Verify vendor
    @Transactional
    public Vendor verifyVendor(Long vendorId, Boolean verified) {
        Vendor vendor = getVendorById(vendorId);
        vendor.setIsVerified(verified);
        return vendorRepository.save(vendor);
    }

    // Admin: Activate/Deactivate vendor
    @Transactional
    public Vendor toggleVendorStatus(Long vendorId, Boolean active) {
        Vendor vendor = getVendorById(vendorId);
        vendor.setIsActive(active);
        return vendorRepository.save(vendor);
    }

    // Delete vendor (soft delete by deactivating)
    @Transactional
    public void deleteVendor(String email) {
        Vendor vendor = getVendorByEmail(email);
        vendor.setIsActive(false);
        vendorRepository.save(vendor);
    }
}

