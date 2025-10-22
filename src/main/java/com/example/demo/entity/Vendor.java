package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "vendors")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @JsonIgnoreProperties({"vendor", "addresses", "orders", "password"})
    private User user;

    @Column(unique = true, nullable = false)
    private String storeName;

    @Column(length = 2000)
    private String description;

    private String businessLicense;
    private String taxId;

    private String logoUrl;
    private String bannerUrl;

    private String businessEmail;
    private String businessPhone;
    private String businessAddress;

    private Boolean isVerified = false;
    private Boolean isActive = true;

    private Double rating = 0.0;
    private Integer reviewCount = 0;

    private Double commission = 10.0; // Platform commission percentage

    @JsonIgnore
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
    private List<Product> products;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
