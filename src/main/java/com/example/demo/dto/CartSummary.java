package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartSummary {
    private Integer totalItems;
    private Double subtotal;
    private Double shippingFee;
    private Double tax;
    private Double total;
}
