package com.ecommerce.order.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private LocalDateTime orderDate;

    @Column(name = "shipping_address_id")
    private Long shippingAddressId;

    private String paymentMethod;
    private String paypalOrderId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    private Double totalAmount;
    private Double shippingFee = 0.0;
    private Double discount = 0.0;
    private Double taxAmount = 0.0;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    private String trackingNumber;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime deliveredDate;

    private String cancellationReason;
    private LocalDateTime cancelledAt;

    @Column(length = 1000)
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
    }
}
