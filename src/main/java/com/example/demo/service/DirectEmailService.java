package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${admin.notification.email}")
    private String adminEmail;

    @Value("${admin.notification.enabled:true}")
    private boolean adminNotificationEnabled;

    @Async
    public void sendOrderCreatedEmail(Order order) {
        sendOrderEmail(order, "ORDER_CREATED", "Order Confirmation");
        if (adminNotificationEnabled) {
            sendAdminEmail(order, "ORDER_CREATED", "[ADMIN] New Order Received");
        }
    }

    @Async
    public void sendOrderConfirmedEmail(Order order) {
        sendOrderEmail(order, "ORDER_CONFIRMED", "Order Confirmed");
        if (adminNotificationEnabled) {
            sendAdminEmail(order, "ORDER_CONFIRMED", "[ADMIN] Order Confirmed");
        }
    }

    @Async
    public void sendPaymentCompletedEmail(Order order) {
        sendOrderEmail(order, "PAYMENT_COMPLETED", "Payment Received");
        if (adminNotificationEnabled) {
            sendAdminEmail(order, "PAYMENT_COMPLETED", "[ADMIN] Payment Received");
        }
    }

    @Async
    public void sendOrderShippedEmail(Order order) {
        sendOrderEmail(order, "ORDER_SHIPPED", "Order Shipped");
        if (adminNotificationEnabled) {
            sendAdminEmail(order, "ORDER_SHIPPED", "[ADMIN] Order Shipped");
        }
    }

    @Async
    public void sendOrderDeliveredEmail(Order order) {
        sendOrderEmail(order, "ORDER_DELIVERED", "Order Delivered");
        if (adminNotificationEnabled) {
            sendAdminEmail(order, "ORDER_DELIVERED", "[ADMIN] Order Delivered");
        }
    }

    @Async
    public void sendOrderCancelledEmail(Order order) {
        sendOrderEmail(order, "ORDER_CANCELLED", "Order Cancelled");
        if (adminNotificationEnabled) {
            sendAdminEmail(order, "ORDER_CANCELLED", "[ADMIN] Order Cancelled");
        }
    }

    private void sendOrderEmail(Order order, String notificationType, String subjectPrefix) {
        try {
            log.info("📧 Attempting to send email to USER {} for order #{} - Type: {}",
                    order.getUser().getEmail(),
                    order.getId(),
                    notificationType);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(order.getUser().getEmail());
            helper.setSubject(subjectPrefix + " - Order #" + order.getId());

            String htmlContent = buildEmailHtml(order, notificationType, false);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("✅ Email sent successfully to USER {} for order #{} - Type: {}",
                    order.getUser().getEmail(),
                    order.getId(),
                    notificationType);
        } catch (MessagingException e) {
            log.error("❌ Failed to send email to USER {} for order #{}: {}",
                    order.getUser().getEmail(),
                    order.getId(),
                    e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error sending email to USER {} for order #{}: {}",
                    order.getUser().getEmail(),
                    order.getId(),
                    e.getMessage());
        }
    }

    private void sendAdminEmail(Order order, String notificationType, String subjectPrefix) {
        try {
            log.info("📧 Attempting to send email to ADMIN {} for order #{} - Type: {}",
                    adminEmail,
                    order.getId(),
                    notificationType);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject(subjectPrefix + " - Order #" + order.getId());

            String htmlContent = buildEmailHtml(order, notificationType, true);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("✅ Email sent successfully to ADMIN {} for order #{} - Type: {}",
                    adminEmail,
                    order.getId(),
                    notificationType);
        } catch (MessagingException e) {
            log.error("❌ Failed to send email to ADMIN {} for order #{}: {}",
                    adminEmail,
                    order.getId(),
                    e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error sending email to ADMIN {} for order #{}: {}",
                    adminEmail,
                    order.getId(),
                    e.getMessage());
        }
    }

    private String buildEmailHtml(Order order, String notificationType, boolean isAdmin) {
        String userName = order.getUser().getUsername() != null
            ? order.getUser().getUsername()
            : order.getUser().getEmail();

        String shippingAddress = order.getShippingAddress() != null
            ? String.format("%s, %s, %s, %s - %s",
                order.getShippingAddress().getAddressLine1(),
                order.getShippingAddress().getCity(),
                order.getShippingAddress().getState(),
                order.getShippingAddress().getCountry(),
                order.getShippingAddress().getPostalCode())
            : "N/A";

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; background: #ffffff; }");

        if (isAdmin) {
            html.append(".header { background: linear-gradient(135deg, #FF5722 0%, #E91E63 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }");
            html.append(".admin-badge { background: #fff; color: #FF5722; padding: 5px 15px; border-radius: 20px; font-weight: bold; display: inline-block; margin-bottom: 10px; }");
        } else {
            html.append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }");
        }

        html.append(".header h1 { margin: 0; font-size: 28px; }");
        html.append(".content { padding: 30px; background: #f9f9f9; }");
        html.append(".greeting { font-size: 18px; margin-bottom: 20px; color: #333; }");
        html.append(".order-info { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");

        if (isAdmin) {
            html.append(".order-info h2 { color: #FF5722; margin-top: 0; border-bottom: 2px solid #FF5722; padding-bottom: 10px; }");
        } else {
            html.append(".order-info h2 { color: #667eea; margin-top: 0; border-bottom: 2px solid #667eea; padding-bottom: 10px; }");
        }

        html.append(".info-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #eee; }");
        html.append(".info-label { font-weight: bold; color: #555; }");
        html.append(".info-value { color: #333; }");
        html.append(".items-table { width: 100%; border-collapse: collapse; margin: 20px 0; background: white; border-radius: 8px; overflow: hidden; }");

        if (isAdmin) {
            html.append(".items-table th { background: #FF5722; color: white; padding: 12px; text-align: left; }");
        } else {
            html.append(".items-table th { background: #667eea; color: white; padding: 12px; text-align: left; }");
        }

        html.append(".items-table td { padding: 12px; border-bottom: 1px solid #eee; }");
        html.append(".items-table tr:last-child td { border-bottom: none; }");

        if (isAdmin) {
            html.append(".total-section { background: #FF5722; color: white; padding: 20px; border-radius: 8px; margin: 20px 0; }");
        } else {
            html.append(".total-section { background: #667eea; color: white; padding: 20px; border-radius: 8px; margin: 20px 0; }");
        }

        html.append(".total-row { display: flex; justify-content: space-between; padding: 5px 0; }");
        html.append(".total-label { font-size: 16px; }");
        html.append(".total-amount { font-size: 24px; font-weight: bold; }");
        html.append(".footer { text-align: center; padding: 20px; color: #888; font-size: 14px; }");
        html.append(".status-badge { display: inline-block; padding: 8px 16px; border-radius: 20px; font-weight: bold; margin: 10px 0; }");
        html.append(".status-pending { background: #fff3cd; color: #856404; }");
        html.append(".status-confirmed { background: #d4edda; color: #155724; }");
        html.append(".status-shipped { background: #d1ecf1; color: #0c5460; }");
        html.append(".status-delivered { background: #d4edda; color: #155724; }");
        html.append(".status-cancelled { background: #f8d7da; color: #721c24; }");
        html.append("</style></head><body>");

        html.append("<div class='container'>");
        html.append("<div class='header'>");

        if (isAdmin) {
            html.append("<span class='admin-badge'>🔔 ADMIN NOTIFICATION</span><br>");
        }

        html.append("<h1>").append(getNotificationTitle(notificationType)).append("</h1>");
        html.append("</div>");

        html.append("<div class='content'>");

        if (isAdmin) {
            html.append("<p class='greeting'>Hello <strong>Admin</strong>,</p>");
            html.append("<p>").append(getAdminNotificationMessage(notificationType, order.getId())).append("</p>");
            html.append("<p><strong>Customer Details:</strong> ").append(userName).append(" (").append(order.getUser().getEmail()).append(")</p>");
        } else {
            html.append("<p class='greeting'>Hello <strong>").append(userName).append("</strong>,</p>");
            html.append("<p>").append(getNotificationMessage(notificationType, order.getId())).append("</p>");
        }

        // Order Information
        html.append("<div class='order-info'>");
        html.append("<h2>Order Details</h2>");
        html.append("<div class='info-row'><span class='info-label'>Order ID:</span><span class='info-value'>#").append(order.getId()).append("</span></div>");
        html.append("<div class='info-row'><span class='info-label'>Order Date:</span><span class='info-value'>").append(formatDate(order.getOrderDate())).append("</span></div>");
        html.append("<div class='info-row'><span class='info-label'>Status:</span><span class='info-value'><span class='status-badge status-").append(order.getOrderStatus().name().toLowerCase()).append("'>").append(order.getOrderStatus()).append("</span></span></div>");
        html.append("<div class='info-row'><span class='info-label'>Payment Method:</span><span class='info-value'>").append(order.getPaymentMethod()).append("</span></div>");
        html.append("<div class='info-row'><span class='info-label'>Payment Status:</span><span class='info-value'>").append(order.getPaymentStatus()).append("</span></div>");

        if (order.getEstimatedDeliveryDate() != null) {
            html.append("<div class='info-row'><span class='info-label'>Estimated Delivery:</span><span class='info-value'>").append(formatDate(order.getEstimatedDeliveryDate())).append("</span></div>");
        }

        if (order.getTrackingNumber() != null && !order.getTrackingNumber().isEmpty()) {
            html.append("<div class='info-row'><span class='info-label'>Tracking Number:</span><span class='info-value'>").append(order.getTrackingNumber()).append("</span></div>");
        }
        html.append("</div>");

        // Shipping Address
        html.append("<div class='order-info'>");
        html.append("<h2>Shipping Address</h2>");
        html.append("<p>").append(shippingAddress).append("</p>");
        html.append("</div>");

        // Order Items
        html.append("<table class='items-table'>");
        html.append("<thead><tr><th>Product</th><th>Quantity</th><th>Price</th><th>Subtotal</th></tr></thead>");
        html.append("<tbody>");

        for (OrderItem item : order.getItems()) {
            double subtotal = item.getPrice() * item.getQuantity();
            html.append("<tr>");
            html.append("<td>").append(item.getProduct().getName()).append("</td>");
            html.append("<td>").append(item.getQuantity()).append("</td>");
            html.append("<td>").append(formatCurrency(item.getPrice())).append("</td>");
            html.append("<td>").append(formatCurrency(subtotal)).append("</td>");
            html.append("</tr>");
        }

        html.append("</tbody></table>");

        // Total Section
        html.append("<div class='total-section'>");
        double subtotal = order.getTotalAmount() - order.getShippingFee() - order.getTaxAmount();
        html.append("<div class='total-row'><span class='total-label'>Subtotal:</span><span>").append(formatCurrency(subtotal)).append("</span></div>");
        html.append("<div class='total-row'><span class='total-label'>Shipping Fee:</span><span>").append(formatCurrency(order.getShippingFee())).append("</span></div>");
        html.append("<div class='total-row'><span class='total-label'>Tax:</span><span>").append(formatCurrency(order.getTaxAmount())).append("</span></div>");
        html.append("<hr style='border: none; border-top: 2px solid rgba(255,255,255,0.3); margin: 10px 0;'>");
        html.append("<div class='total-row'><span class='total-label'>Total Amount:</span><span class='total-amount'>").append(formatCurrency(order.getTotalAmount())).append("</span></div>");
        html.append("</div>");

        if (isAdmin) {
            html.append("<div style='background: #fff3cd; color: #856404; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #ffc107;'>");
            html.append("<h3 style='margin-top: 0;'>⚠️ Action Required</h3>");
            html.append("<p style='margin-bottom: 0;'>Please review and process this order accordingly. Take necessary actions based on the order status and payment method.</p>");
            html.append("</div>");
        } else {
            // Thank You Section - More prominent
            html.append("<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 25px; border-radius: 8px; margin: 30px 0; text-align: center;'>");
            html.append("<h2 style='margin: 0 0 15px 0; font-size: 26px;'>🙏 Thank You for Your Order!</h2>");
            html.append("<p style='margin: 0; font-size: 16px; line-height: 1.8;'>");
            html.append("We truly appreciate your business and trust in our store.<br>");
            html.append("Your satisfaction is our priority, and we're committed to providing you with the best shopping experience.<br>");
            html.append("We hope you enjoy your purchase!");
            html.append("</p>");
            html.append("</div>");

            html.append("<p style='margin-top: 20px; color: #555; text-align: center; font-size: 15px;'>");
            html.append("If you have any questions about your order, please don't hesitate to contact our support team.<br>");
            html.append("We're here to help you!");
            html.append("</p>");
        }

        html.append("</div>");

        html.append("<div class='footer'>");
        html.append("<p>&copy; 2025 Your E-commerce Store. All rights reserved.</p>");
        html.append("<p>This is an automated email. Please do not reply to this message.</p>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body></html>");

        return html.toString();
    }

    private String getNotificationTitle(String notificationType) {
        return switch (notificationType) {
            case "ORDER_CREATED" -> "🎉 Order Confirmation";
            case "ORDER_CONFIRMED" -> "✅ Order Confirmed";
            case "PAYMENT_COMPLETED" -> "💳 Payment Received";
            case "ORDER_SHIPPED" -> "📦 Order Shipped";
            case "ORDER_DELIVERED" -> "🎊 Order Delivered";
            case "ORDER_CANCELLED" -> "❌ Order Cancelled";
            default -> "📧 Order Update";
        };
    }

    private String getNotificationMessage(String notificationType, Long orderId) {
        return switch (notificationType) {
            case "ORDER_CREATED" -> "Thank you for your order! We have received your order #" + orderId + " and it's being processed.";
            case "ORDER_CONFIRMED" -> "Great news! Your order #" + orderId + " has been confirmed and will be prepared for shipping soon.";
            case "PAYMENT_COMPLETED" -> "We have received your payment for order #" + orderId + ". Your order will be processed shortly.";
            case "ORDER_SHIPPED" -> "Your order #" + orderId + " has been shipped! You can track your package using the tracking number below.";
            case "ORDER_DELIVERED" -> "Congratulations! Your order #" + orderId + " has been delivered. We hope you enjoy your purchase!";
            case "ORDER_CANCELLED" -> "Your order #" + orderId + " has been cancelled as requested.";
            default -> "There's an update on your order #" + orderId + ".";
        };
    }

    private String getAdminNotificationMessage(String notificationType, Long orderId) {
        return switch (notificationType) {
            case "ORDER_CREATED" -> "A new order has been placed. Order #" + orderId + " requires your attention.";
            case "ORDER_CONFIRMED" -> "Order #" + orderId + " has been confirmed and is ready for processing.";
            case "PAYMENT_COMPLETED" -> "Payment has been received for order #" + orderId + ". Please proceed with fulfillment.";
            case "ORDER_SHIPPED" -> "Order #" + orderId + " has been marked as shipped.";
            case "ORDER_DELIVERED" -> "Order #" + orderId + " has been successfully delivered to the customer.";
            case "ORDER_CANCELLED" -> "Order #" + orderId + " has been cancelled by the customer.";
            default -> "There's an update on order #" + orderId + " that requires your attention.";
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
