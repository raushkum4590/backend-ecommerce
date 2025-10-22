package com.example.demo.controller;

import com.example.demo.entity.PaymentStatus;
import lombok.Data;

@Data
public class PaymentStatusUpdateRequest {
    private PaymentStatus status;
}

