package com.ecommerce.order.service;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order createOrder(Order order) {
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }
        if (order.getOrderStatus() == null) {
            order.setOrderStatus(OrderStatus.PENDING);
        }
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setOrderStatus(status);

        if (status == OrderStatus.DELIVERED) {
            order.setDeliveredDate(LocalDateTime.now());
        }

        return orderRepository.save(order);
    }

    public Order cancelOrder(Long id, String reason) {
        Order order = getOrderById(id);
        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel delivered order");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);
        order.setCancelledAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
