package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;
    private final DirectEmailService directEmailService;

    @Autowired(required = false)
    private OrderNotificationProducer orderNotificationProducer;

    @Transactional
    public Order createOrder(User user, Long addressId, String paymentMethod) {
        // Validate addressId
        if (addressId == null) {
            throw new RuntimeException("Address ID is required. Please select a delivery address.");
        }

        List<CartItem> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Address shippingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + addressId + ". Please add a delivery address first."));

        if (!shippingAddress.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Address does not belong to user");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setOrderStatus(OrderStatus.PENDING);

        // Handle payment status based on payment method
        if ("cash".equalsIgnoreCase(paymentMethod) || "cod".equalsIgnoreCase(paymentMethod)) {
            order.setPaymentStatus(PaymentStatus.CASH_ON_DELIVERY);
            order.setOrderStatus(OrderStatus.CONFIRMED); // Auto-confirm COD orders
        } else {
            order.setPaymentStatus(PaymentStatus.PENDING);
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);

            // Update product stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            totalAmount += product.getPrice() * cartItem.getQuantity();
        }

        // Calculate additional costs
        double shippingFee = calculateShippingFee(totalAmount);
        double taxAmount = calculateTax(totalAmount);

        order.setItems(orderItems);
        order.setShippingFee(shippingFee);
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(totalAmount + shippingFee + taxAmount);
        order.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(7));

        Order savedOrder = orderRepository.save(order);

        // Clear the cart after successful order
        cartService.clearCart(user);

        // Send email notification directly (always works)
        try {
            directEmailService.sendOrderCreatedEmail(savedOrder);
            if ("cash".equalsIgnoreCase(paymentMethod) || "cod".equalsIgnoreCase(paymentMethod)) {
                directEmailService.sendOrderConfirmedEmail(savedOrder);
            }
        } catch (Exception e) {
            // Log error but don't fail the order creation
            System.err.println("Failed to send email notification: " + e.getMessage());
        }

        // Send Kafka notification - this will trigger email to user (only if Kafka is enabled)
        if (orderNotificationProducer != null) {
            try {
                orderNotificationProducer.sendOrderCreatedNotification(savedOrder);
                if ("cash".equalsIgnoreCase(paymentMethod) || "cod".equalsIgnoreCase(paymentMethod)) {
                    orderNotificationProducer.sendOrderConfirmedNotification(savedOrder);
                }
            } catch (Exception e) {
                // Log error but don't fail the order creation
                System.err.println("Failed to send order notification: " + e.getMessage());
            }
        }

        return savedOrder;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public List<Order> getOrdersByUser(User user) {
        return getUserOrders(user);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatusOrderByOrderDateDesc(status);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        OrderStatus oldStatus = order.getOrderStatus();
        order.setOrderStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredDate(LocalDateTime.now());
        }

        Order savedOrder = orderRepository.save(order);

        // Send direct email notification for status updates
        try {
            switch (newStatus) {
                case SHIPPED -> directEmailService.sendOrderShippedEmail(savedOrder);
                case DELIVERED -> directEmailService.sendOrderDeliveredEmail(savedOrder);
                case CANCELLED -> directEmailService.sendOrderCancelledEmail(savedOrder);
                default -> {}
            }
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }

        // Send Kafka notification for status updates (only if Kafka is enabled)
        if (orderNotificationProducer != null) {
            try {
                if (newStatus == OrderStatus.SHIPPED) {
                    orderNotificationProducer.sendOrderShippedNotification(savedOrder);
                } else if (newStatus == OrderStatus.DELIVERED) {
                    orderNotificationProducer.sendOrderDeliveredNotification(savedOrder);
                } else if (newStatus == OrderStatus.CANCELLED) {
                    orderNotificationProducer.sendOrderCancelledNotification(savedOrder);
                }
            } catch (Exception e) {
                System.err.println("Failed to send Kafka notification: " + e.getMessage());
            }
        }

        return savedOrder;
    }

    @Transactional
    public Order updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        Order order = getOrderById(orderId);
        order.setPaymentStatus(paymentStatus);

        if (paymentStatus == PaymentStatus.COMPLETED) {
            order.setOrderStatus(OrderStatus.CONFIRMED);
        }

        Order savedOrder = orderRepository.save(order);

        // Send direct email notification for payment completion
        try {
            if (paymentStatus == PaymentStatus.COMPLETED) {
                directEmailService.sendPaymentCompletedEmail(savedOrder);
            }
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }

        // Send Kafka notification for payment updates (only if Kafka is enabled)
        if (orderNotificationProducer != null) {
            try {
                if (paymentStatus == PaymentStatus.COMPLETED) {
                    orderNotificationProducer.sendPaymentCompletedNotification(savedOrder);
                }
            } catch (Exception e) {
                System.err.println("Failed to send payment notification: " + e.getMessage());
            }
        }

        return savedOrder;
    }

    @Transactional
    public Order cancelOrder(Long orderId, String reason) {
        Order order = getOrderById(orderId);

        if (order.getOrderStatus() == OrderStatus.DELIVERED ||
            order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel this order");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);
        order.setCancelledAt(LocalDateTime.now());

        // Restore product stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        Order savedOrder = orderRepository.save(order);

        // Send direct email notification for cancellation
        try {
            directEmailService.sendOrderCancelledEmail(savedOrder);
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }

        // Send Kafka notification for cancellation (only if Kafka is enabled)
        if (orderNotificationProducer != null) {
            try {
                orderNotificationProducer.sendOrderCancelledNotification(savedOrder);
            } catch (Exception e) {
                System.err.println("Failed to send cancellation notification: " + e.getMessage());
            }
        }

        return savedOrder;
    }

    @Transactional
    public Order updateTrackingNumber(Long orderId, String trackingNumber) {
        Order order = getOrderById(orderId);
        order.setTrackingNumber(trackingNumber);
        Order savedOrder = orderRepository.save(order);

        // Send direct email notification when tracking number is added
        try {
            if (order.getOrderStatus() == OrderStatus.SHIPPED) {
                directEmailService.sendOrderShippedEmail(savedOrder);
            }
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }

        // Send Kafka notification when tracking number is added (only if Kafka is enabled)
        if (orderNotificationProducer != null) {
            try {
                if (order.getOrderStatus() == OrderStatus.SHIPPED) {
                    orderNotificationProducer.sendOrderShippedNotification(savedOrder);
                }
            } catch (Exception e) {
                System.err.println("Failed to send tracking notification: " + e.getMessage());
            }
        }

        return savedOrder;
    }

    public Double getTotalRevenue() {
        Double revenue = orderRepository.calculateTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    private double calculateShippingFee(double totalAmount) {
        if (totalAmount >= 50.0) {
            return 0.0; // Free shipping for orders over $50
        }
        return 5.0;
    }

    private double calculateTax(double totalAmount) {
        return totalAmount * 0.08; // 8% tax
    }
}
