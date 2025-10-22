package com.example.demo.dto;

import lombok.Data;

@Data
public class VendorRegistrationRequest {
    private String storeName;
    private String description;
    private String businessLicense;
    private String taxId;
    private String businessEmail;
    private String businessPhone;
    private String businessAddress;
    private String logoUrl;
    private String bannerUrl;
}

