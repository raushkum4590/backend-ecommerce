package com.example.demo.entity;

import com.example.demo.entity.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendor_id")
    @JsonIgnoreProperties({"products", "user"})
    private Vendor vendor;

    private Double price;
    private Integer stock;
    private String imageUrl;

    @Column(length = 2000)
    private String description;

    private String brand;
    private String unit; // kg, liter, piece, etc.
    private Double weight;
    private Boolean isAvailable = true;
    private Boolean isFeatured = false;
    private Double discount = 0.0; // percentage

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews;

    private Double averageRating = 0.0;
    private Integer reviewCount = 0;

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
