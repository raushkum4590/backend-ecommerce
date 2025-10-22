package com.example.demo.dto;

import lombok.Data;

@Data
public class PaymentConfirmRequest {
    private String paypalOrderId;
    private Long orderId;
}
