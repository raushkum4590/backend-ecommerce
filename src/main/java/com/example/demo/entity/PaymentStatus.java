package com.example.demo.entity;

public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED,
    CASH_ON_DELIVERY  // For COD orders - payment collected on delivery
}
