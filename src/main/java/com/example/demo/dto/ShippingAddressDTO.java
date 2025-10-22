package com.example.demo.dto;

import lombok.Data;

/**
 * DTO for shipping address in payment request
 */
@Data
public class ShippingAddressDTO {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phoneNumber;
}

