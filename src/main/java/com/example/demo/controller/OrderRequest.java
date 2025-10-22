package com.example.demo.controller;

import lombok.Data;

@Data
public class OrderRequest {
    private Long addressId;
    private String paymentMethod;
}
