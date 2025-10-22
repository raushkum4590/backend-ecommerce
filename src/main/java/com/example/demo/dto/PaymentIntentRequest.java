package com.example.demo.dto;

import lombok.Data;

@Data
public class PaymentIntentRequest {
    private Long orderId;
    private String currency = "USD";
    private String returnUrl;
    private String cancelUrl;
}

