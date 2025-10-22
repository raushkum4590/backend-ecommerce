package com.example.demo.dto;

import lombok.Data;

@Data
public class VendorUpdateRequest {
    private String storeName;
    private String description;
    private String businessEmail;
    private String businessPhone;
    private String businessAddress;
    private String logoUrl;
    private String bannerUrl;
}
