package com.example.demo.dto;

import lombok.Data;

import java.util.List;

/**
 * Request DTO for creating order and payment in one step
 */
@Data
public class DirectPaymentRequest {
    private Long addressId;
    private String currency = "USD";
    private String returnUrl;
    private String cancelUrl;
    // Optional: if frontend wants to create order from cart
    private List<OrderItemRequest> items;
}

@Data
class OrderItemRequest {
    private Long productId;
    private Integer quantity;
}

