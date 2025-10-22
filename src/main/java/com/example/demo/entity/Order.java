package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    private String paymentMethod;

    private String paypalOrderId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
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
        orderDate = LocalDateTime.now();
    }
}
