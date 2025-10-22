package com.example.demo.dto;

import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationDTO {
    private Long orderId;
    private String userEmail;
    private String userName;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private Double totalAmount;
    private Double shippingFee;
    private Double taxAmount;
    private String shippingAddress;
    private String trackingNumber;
    private LocalDateTime orderDate;
    private LocalDateTime estimatedDeliveryDate;
    private List<OrderItemDTO> items;
    private String notificationType; // e.g., "ORDER_CREATED", "ORDER_CONFIRMED", "PAYMENT_COMPLETED", "ORDER_SHIPPED", "ORDER_DELIVERED"
    private LocalDateTime notificationTime;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private String productName;
        private Integer quantity;
        private Double price;
        private String vendorName;
    }
}

