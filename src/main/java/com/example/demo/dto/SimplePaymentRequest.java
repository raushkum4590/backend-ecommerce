package com.example.demo.dto;

import lombok.Data;

/**
 * Request DTO for simplified payment with inline shipping address
 */
@Data
public class SimplePaymentRequest {
    private ShippingAddressDTO shippingAddress;
    private String currency = "USD";
    private String returnUrl;
    private String cancelUrl;
}

