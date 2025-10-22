package com.example.demo.controller;

import lombok.Data;

@Data
public class CartRequest {
    private Long productId;
    private Integer quantity;
}
