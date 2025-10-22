package com.example.demo.service;

import com.example.demo.config.KafkaConfig;
import com.example.demo.dto.OrderNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = false)
public class OrderNotificationConsumer {

    private final EmailNotificationService emailNotificationService;

    @KafkaListener(topics = KafkaConfig.ORDER_NOTIFICATION_TOPIC, groupId = "order-notification-group")
    public void consumeOrderNotification(@Payload OrderNotificationDTO notification,
                                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                          @Header(KafkaHeaders.OFFSET) Long offset) {
        try {
            log.info("📨 Received order notification from topic: {} at offset: {}", topic, offset);
            log.info("📋 Notification Details - Order ID: {}, Type: {}, User: {}",
                    notification.getOrderId(),
                    notification.getNotificationType(),
                    notification.getUserEmail());

            // Send email to user
            emailNotificationService.sendOrderNotificationEmail(notification);

            log.info("✅ Successfully processed order notification for Order ID: {}", notification.getOrderId());
        } catch (Exception e) {
            log.error("❌ Error processing order notification for Order ID: {} - Error: {}",
                    notification.getOrderId(),
                    e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaConfig.ORDER_STATUS_TOPIC, groupId = "order-notification-group")
    public void consumeOrderStatusUpdate(@Payload OrderNotificationDTO notification,
                                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                          @Header(KafkaHeaders.OFFSET) Long offset) {
        try {
            log.info("📨 Received order status update from topic: {} at offset: {}", topic, offset);
            log.info("📋 Status Update - Order ID: {}, New Status: {}, Type: {}",
                    notification.getOrderId(),
                    notification.getOrderStatus(),
                    notification.getNotificationType());

            // Send email to user about status update
            emailNotificationService.sendOrderNotificationEmail(notification);

            log.info("✅ Successfully processed order status update for Order ID: {}", notification.getOrderId());
        } catch (Exception e) {
            log.error("❌ Error processing order status update for Order ID: {} - Error: {}",
                    notification.getOrderId(),
                    e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaConfig.PAYMENT_NOTIFICATION_TOPIC, groupId = "order-notification-group")
    public void consumePaymentNotification(@Payload OrderNotificationDTO notification,
                                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                            @Header(KafkaHeaders.OFFSET) Long offset) {
        try {
            log.info("📨 Received payment notification from topic: {} at offset: {}", topic, offset);
            log.info("💳 Payment Details - Order ID: {}, Status: {}, Amount: ${}",
                    notification.getOrderId(),
                    notification.getPaymentStatus(),
                    notification.getTotalAmount());

            // Send email to user about payment
            emailNotificationService.sendOrderNotificationEmail(notification);

            log.info("✅ Successfully processed payment notification for Order ID: {}", notification.getOrderId());
        } catch (Exception e) {
            log.error("❌ Error processing payment notification for Order ID: {} - Error: {}",
                    notification.getOrderId(),
                    e.getMessage(), e);
        }
    }
}
