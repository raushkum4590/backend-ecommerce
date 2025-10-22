package com.example.demo.service;

import com.example.demo.dto.OrderNotificationDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${admin.notification.email}")
    private String adminEmail;

    @Value("${admin.notification.enabled:true}")
    private boolean adminNotificationEnabled;

    public void sendOrderNotificationEmail(OrderNotificationDTO notification) {
        // Send email to user
        sendEmailToUser(notification);

        // Send email to admin if enabled
        if (adminNotificationEnabled) {
            sendEmailToAdmin(notification);
        }
    }

    private void sendEmailToUser(OrderNotificationDTO notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(notification.getUserEmail());
            helper.setSubject(getEmailSubject(notification));

            String htmlContent = buildEmailContent(notification, false);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("✉️ Email sent successfully to USER {} for order #{} - Type: {}",
                    notification.getUserEmail(),
                    notification.getOrderId(),
                    notification.getNotificationType());
        } catch (MessagingException e) {
            log.error("❌ Failed to send email to USER {} for order #{}: {}",
                    notification.getUserEmail(),
                    notification.getOrderId(),
                    e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error sending email to USER: {}", e.getMessage(), e);
        }
    }

    private void sendEmailToAdmin(OrderNotificationDTO notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject(getAdminEmailSubject(notification));

            String htmlContent = buildEmailContent(notification, true);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("✉️ Email sent successfully to ADMIN {} for order #{} - Type: {}",
                    adminEmail,
                    notification.getOrderId(),
                    notification.getNotificationType());
        } catch (MessagingException e) {
            log.error("❌ Failed to send email to ADMIN {} for order #{}: {}",
                    adminEmail,
                    notification.getOrderId(),
                    e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error sending email to ADMIN: {}", e.getMessage(), e);
        }
    }

    private String getEmailSubject(OrderNotificationDTO notification) {
        return switch (notification.getNotificationType()) {
            case "ORDER_CREATED" -> "Order Confirmation - Order #" + notification.getOrderId();
            case "ORDER_CONFIRMED" -> "Your Order Has Been Confirmed - Order #" + notification.getOrderId();
            case "PAYMENT_COMPLETED" -> "Payment Received - Order #" + notification.getOrderId();
            case "ORDER_SHIPPED" -> "Your Order Has Been Shipped - Order #" + notification.getOrderId();
            case "ORDER_DELIVERED" -> "Your Order Has Been Delivered - Order #" + notification.getOrderId();
            case "ORDER_CANCELLED" -> "Order Cancelled - Order #" + notification.getOrderId();
            default -> "Order Update - Order #" + notification.getOrderId();
        };
    }

    private String getAdminEmailSubject(OrderNotificationDTO notification) {
        return switch (notification.getNotificationType()) {
            case "ORDER_CREATED" -> "[ADMIN] New Order Received - Order #" + notification.getOrderId();
            case "ORDER_CONFIRMED" -> "[ADMIN] Order Confirmed - Order #" + notification.getOrderId();
            case "PAYMENT_COMPLETED" -> "[ADMIN] Payment Received - Order #" + notification.getOrderId();
            case "ORDER_SHIPPED" -> "[ADMIN] Order Shipped - Order #" + notification.getOrderId();
            case "ORDER_DELIVERED" -> "[ADMIN] Order Delivered - Order #" + notification.getOrderId();
            case "ORDER_CANCELLED" -> "[ADMIN] Order Cancelled - Order #" + notification.getOrderId();
            default -> "[ADMIN] Order Update - Order #" + notification.getOrderId();
        };
    }

    private String buildEmailContent(OrderNotificationDTO notification, boolean isAdmin) {
        Context context = new Context();
        context.setVariable("notification", notification);
        context.setVariable("userName", notification.getUserName());
        context.setVariable("orderId", notification.getOrderId());
        context.setVariable("orderStatus", notification.getOrderStatus());
        context.setVariable("paymentStatus", notification.getPaymentStatus());
        context.setVariable("totalAmount", formatCurrency(notification.getTotalAmount()));
        context.setVariable("shippingFee", formatCurrency(notification.getShippingFee()));
        context.setVariable("taxAmount", formatCurrency(notification.getTaxAmount()));
        context.setVariable("orderDate", formatDate(notification.getOrderDate()));
        context.setVariable("estimatedDelivery", formatDate(notification.getEstimatedDeliveryDate()));
        context.setVariable("trackingNumber", notification.getTrackingNumber());
        context.setVariable("shippingAddress", notification.getShippingAddress());
        context.setVariable("items", notification.getItems());
        context.setVariable("isAdmin", isAdmin);

        // Try to use template, fallback to simple HTML if template doesn't exist
        try {
            return templateEngine.process("email/order-notification", context);
        } catch (Exception e) {
            log.warn("Template not found, using simple HTML email");
            return buildSimpleHtmlEmail(notification, isAdmin);
        }
    }

    private String buildSimpleHtmlEmail(OrderNotificationDTO notification, boolean isAdmin) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");

        if (isAdmin) {
            html.append(".header { background: #FF5722; color: white; padding: 20px; text-align: center; }");
        } else {
            html.append(".header { background: #4CAF50; color: white; padding: 20px; text-align: center; }");
        }

        html.append(".content { padding: 20px; background: #f9f9f9; }");
        html.append(".order-details { background: white; padding: 15px; margin: 10px 0; border-left: 4px solid #4CAF50; }");
        html.append(".item { padding: 10px 0; border-bottom: 1px solid #eee; }");
        html.append(".total { font-size: 18px; font-weight: bold; color: #4CAF50; }");
        html.append(".footer { text-align: center; padding: 20px; color: #777; font-size: 12px; }");

        if (isAdmin) {
            html.append(".admin-badge { background: #FF5722; color: white; padding: 5px 10px; border-radius: 3px; font-weight: bold; }");
        }

        html.append("</style></head><body><div class='container'>");

        // Header
        html.append("<div class='header'>");
        if (isAdmin) {
            html.append("<span class='admin-badge'>ADMIN NOTIFICATION</span><br>");
        }
        html.append("<h1>").append(getEmailTitle(notification, isAdmin)).append("</h1></div>");

        // Content
        html.append("<div class='content'>");

        if (isAdmin) {
            html.append("<p>Dear Admin,</p>");
            html.append("<p>").append(getAdminEmailMessage(notification)).append("</p>");
            html.append("<p><strong>Customer:</strong> ").append(notification.getUserName()).append(" (").append(notification.getUserEmail()).append(")</p>");
        } else {
            html.append("<p>Dear ").append(notification.getUserName()).append(",</p>");
            html.append("<p>").append(getEmailMessage(notification)).append("</p>");
        }

        // Order Details
        html.append("<div class='order-details'>");
        html.append("<h3>Order Details</h3>");
        html.append("<p><strong>Order ID:</strong> #").append(notification.getOrderId()).append("</p>");
        html.append("<p><strong>Order Date:</strong> ").append(formatDate(notification.getOrderDate())).append("</p>");
        html.append("<p><strong>Order Status:</strong> ").append(notification.getOrderStatus()).append("</p>");
        html.append("<p><strong>Payment Status:</strong> ").append(notification.getPaymentStatus()).append("</p>");
        html.append("<p><strong>Payment Method:</strong> ").append(notification.getPaymentMethod()).append("</p>");

        if (notification.getTrackingNumber() != null) {
            html.append("<p><strong>Tracking Number:</strong> ").append(notification.getTrackingNumber()).append("</p>");
        }

        if (notification.getEstimatedDeliveryDate() != null) {
            html.append("<p><strong>Estimated Delivery:</strong> ").append(formatDate(notification.getEstimatedDeliveryDate())).append("</p>");
        }

        html.append("<p><strong>Shipping Address:</strong><br>").append(notification.getShippingAddress()).append("</p>");
        html.append("</div>");

        // Order Items
        html.append("<div class='order-details'>");
        html.append("<h3>Order Items</h3>");
        for (OrderNotificationDTO.OrderItemDTO item : notification.getItems()) {
            html.append("<div class='item'>");
            html.append("<strong>").append(item.getProductName()).append("</strong><br>");
            html.append("Quantity: ").append(item.getQuantity()).append(" × ").append(formatCurrency(item.getPrice()));
            html.append(" = ").append(formatCurrency(item.getQuantity() * item.getPrice())).append("<br>");
            html.append("<small>Vendor: ").append(item.getVendorName()).append("</small>");
            html.append("</div>");
        }
        html.append("</div>");

        // Price Summary
        html.append("<div class='order-details'>");
        html.append("<h3>Price Summary</h3>");
        html.append("<p>Subtotal: ").append(formatCurrency(notification.getTotalAmount() - notification.getShippingFee() - notification.getTaxAmount())).append("</p>");
        html.append("<p>Shipping Fee: ").append(formatCurrency(notification.getShippingFee())).append("</p>");
        html.append("<p>Tax: ").append(formatCurrency(notification.getTaxAmount())).append("</p>");
        html.append("<p class='total'>Total: ").append(formatCurrency(notification.getTotalAmount())).append("</p>");
        html.append("</div>");

        if (isAdmin) {
            html.append("<p><strong>Action Required:</strong> Please process this order accordingly.</p>");
        } else {
            html.append("<p>Thank you for shopping with us!</p>");
        }

        html.append("</div>");

        // Footer
        html.append("<div class='footer'>");
        html.append("<p>This is an automated email. Please do not reply.</p>");
        html.append("<p>&copy; 2025 Grocery E-commerce. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");
        return html.toString();
    }

    private String getEmailTitle(OrderNotificationDTO notification, boolean isAdmin) {
        String prefix = isAdmin ? "[ADMIN] " : "";
        return switch (notification.getNotificationType()) {
            case "ORDER_CREATED" -> prefix + "Order Confirmation";
            case "ORDER_CONFIRMED" -> prefix + "Order Confirmed";
            case "PAYMENT_COMPLETED" -> prefix + "Payment Received";
            case "ORDER_SHIPPED" -> prefix + "Order Shipped";
            case "ORDER_DELIVERED" -> prefix + "Order Delivered";
            case "ORDER_CANCELLED" -> prefix + "Order Cancelled";
            default -> prefix + "Order Update";
        };
    }

    private String getEmailMessage(OrderNotificationDTO notification) {
        return switch (notification.getNotificationType()) {
            case "ORDER_CREATED" -> "Your order has been successfully placed and is being processed.";
            case "ORDER_CONFIRMED" -> "Great news! Your order has been confirmed and will be prepared for shipping.";
            case "PAYMENT_COMPLETED" -> "We have received your payment. Your order will be processed shortly.";
            case "ORDER_SHIPPED" -> "Your order is on its way! You can track your package using the tracking number below.";
            case "ORDER_DELIVERED" -> "Your order has been delivered. We hope you enjoy your purchase!";
            case "ORDER_CANCELLED" -> "Your order has been cancelled as requested.";
            default -> "There's an update on your order.";
        };
    }

    private String getAdminEmailMessage(OrderNotificationDTO notification) {
        return switch (notification.getNotificationType()) {
            case "ORDER_CREATED" -> "A new order has been placed by " + notification.getUserName() + ".";
            case "ORDER_CONFIRMED" -> "Order #" + notification.getOrderId() + " has been confirmed.";
            case "PAYMENT_COMPLETED" -> "Payment has been received for Order #" + notification.getOrderId() + ".";
            case "ORDER_SHIPPED" -> "Order #" + notification.getOrderId() + " has been shipped.";
            case "ORDER_DELIVERED" -> "Order #" + notification.getOrderId() + " has been delivered to the customer.";
            case "ORDER_CANCELLED" -> "Order #" + notification.getOrderId() + " has been cancelled by the customer.";
            default -> "There's an update on Order #" + notification.getOrderId() + ".";
        };
    }

    private String formatCurrency(Double amount) {
        if (amount == null) return "$0.00";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        return formatter.format(amount);
    }

    private String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        return dateTime.format(formatter);
    }
}

