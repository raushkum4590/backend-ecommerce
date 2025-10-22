package com.example.demo.service;

import com.example.demo.config.KafkaConfig;
import com.example.demo.dto.OrderNotificationDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = false)
public class OrderNotificationProducer {

    private final KafkaTemplate<String, OrderNotificationDTO> kafkaTemplate;

    public void sendOrderCreatedNotification(Order order) {
        OrderNotificationDTO notification = buildOrderNotification(order, "ORDER_CREATED");
        sendNotification(KafkaConfig.ORDER_NOTIFICATION_TOPIC, notification);
    }

    public void sendOrderConfirmedNotification(Order order) {
        OrderNotificationDTO notification = buildOrderNotification(order, "ORDER_CONFIRMED");
        sendNotification(KafkaConfig.ORDER_NOTIFICATION_TOPIC, notification);
    }

    public void sendPaymentCompletedNotification(Order order) {
        OrderNotificationDTO notification = buildOrderNotification(order, "PAYMENT_COMPLETED");
        sendNotification(KafkaConfig.PAYMENT_NOTIFICATION_TOPIC, notification);
    }

    public void sendOrderStatusUpdateNotification(Order order, String statusType) {
        OrderNotificationDTO notification = buildOrderNotification(order, statusType);
        sendNotification(KafkaConfig.ORDER_STATUS_TOPIC, notification);
    }

    public void sendOrderShippedNotification(Order order) {
        OrderNotificationDTO notification = buildOrderNotification(order, "ORDER_SHIPPED");
        sendNotification(KafkaConfig.ORDER_STATUS_TOPIC, notification);
    }

    public void sendOrderDeliveredNotification(Order order) {
        OrderNotificationDTO notification = buildOrderNotification(order, "ORDER_DELIVERED");
        sendNotification(KafkaConfig.ORDER_STATUS_TOPIC, notification);
    }

    public void sendOrderCancelledNotification(Order order) {
        OrderNotificationDTO notification = buildOrderNotification(order, "ORDER_CANCELLED");
        sendNotification(KafkaConfig.ORDER_STATUS_TOPIC, notification);
    }

    private OrderNotificationDTO buildOrderNotification(Order order, String notificationType) {
        String shippingAddress = order.getShippingAddress() != null
            ? String.format("%s, %s, %s, %s - %s",
                order.getShippingAddress().getAddressLine1(),
                order.getShippingAddress().getCity(),
                order.getShippingAddress().getState(),
                order.getShippingAddress().getCountry(),
                order.getShippingAddress().getPostalCode())
            : "N/A";

        String userName = order.getUser().getUsername() != null
            ? order.getUser().getUsername()
            : order.getUser().getEmail();

        return OrderNotificationDTO.builder()
                .orderId(order.getId())
                .userEmail(order.getUser().getEmail())
                .userName(userName)
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .totalAmount(order.getTotalAmount())
                .shippingFee(order.getShippingFee())
                .taxAmount(order.getTaxAmount())
                .shippingAddress(shippingAddress)
                .trackingNumber(order.getTrackingNumber())
                .orderDate(order.getOrderDate())
                .estimatedDeliveryDate(order.getEstimatedDeliveryDate())
                .items(order.getItems().stream()
                        .map(this::buildOrderItemDTO)
                        .collect(Collectors.toList()))
                .notificationType(notificationType)
                .notificationTime(LocalDateTime.now())
                .build();
    }

    private OrderNotificationDTO.OrderItemDTO buildOrderItemDTO(OrderItem item) {
        return OrderNotificationDTO.OrderItemDTO.builder()
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .vendorName(item.getProduct().getVendor() != null
                    ? item.getProduct().getVendor().getStoreName()
                    : "N/A")
                .build();
    }

    private void sendNotification(String topic, OrderNotificationDTO notification) {
        try {
            CompletableFuture<SendResult<String, OrderNotificationDTO>> future =
                kafkaTemplate.send(topic, notification.getOrderId().toString(), notification);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✅ Sent order notification [Type: {}] for Order ID: {} to topic: {} with offset: {}",
                            notification.getNotificationType(),
                            notification.getOrderId(),
                            topic,
                            result.getRecordMetadata().offset());
                    log.info("📧 Notification Details - User: {}, Status: {}, Payment: {}, Total: ${}",
                            notification.getUserEmail(),
                            notification.getOrderStatus(),
                            notification.getPaymentStatus(),
                            notification.getTotalAmount());
                } else {
                    log.error("❌ Failed to send notification for Order ID: {} - Error: {}",
                            notification.getOrderId(),
                            ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("❌ Error sending Kafka notification for Order ID: {} - {}",
                    notification.getOrderId(),
                    e.getMessage(), e);
        }
    }
}
