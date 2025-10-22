package com.example.demo.repository;

import com.example.demo.entity.Vendor;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUser(User user);
    Optional<Vendor> findByUserId(Long userId);
    Optional<Vendor> findByStoreName(String storeName);
    List<Vendor> findByIsVerifiedTrue();
    List<Vendor> findByIsActiveTrue();
    List<Vendor> findByIsVerifiedAndIsActive(Boolean isVerified, Boolean isActive);
}

