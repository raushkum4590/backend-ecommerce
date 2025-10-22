package com.example.demo.dto;

import lombok.Data;

@Data
public class AdminProductRequest {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private String brand;
    private String unit;
    private Double weight;
    private Boolean isAvailable;
    private Boolean isFeatured;
    private Double discount;

    // Category as Long ID instead of object
    private Long categoryId;

    // Vendor as Long ID (optional - null for admin products)
    private Long vendorId;
}

