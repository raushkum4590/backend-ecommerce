package com.ecommerce.product.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "vendors")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String businessName;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private Boolean isApproved = false;
}

