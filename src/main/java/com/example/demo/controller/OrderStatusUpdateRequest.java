package com.example.demo.controller;

import com.example.demo.entity.OrderStatus;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    private OrderStatus status;
}

