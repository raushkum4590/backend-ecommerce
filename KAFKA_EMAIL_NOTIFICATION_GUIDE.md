# 📧 Kafka Real-Time Order Notification System - Complete Setup Guide

## 🎯 Overview

This system implements **real-time email notifications** for order events using **Apache Kafka** as a message broker. When orders are created, updated, or cancelled, notifications are automatically sent to Kafka topics and consumed by email services that send detailed emails to users.

## 🏗️ Architecture

```
Order Created/Updated → Kafka Producer → Kafka Topics → Kafka Consumer → Email Service → User Email
```

### Kafka Topics Created:
1. **order-notifications** - New orders, confirmations
2. **order-status-updates** - Status changes (shipped, delivered, etc.)
3. **payment-notifications** - Payment completions

## 📋 Prerequisites

- Java 17+
- Docker Desktop (for running Kafka)
- Maven
- MySQL Database
- Gmail account with App Password (for sending emails)

## 🚀 Setup Instructions

### Step 1: Configure Email Settings

Edit `src/main/resources/application.properties`:

```properties
# Email Configuration (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com          # Replace with your Gmail
spring.mail.password=your-app-password              # Replace with Gmail App Password
```

**How to get Gmail App Password:**
1. Go to Google Account Settings → Security
2. Enable 2-Step Verification
3. Search for "App Passwords"
4. Generate a new app password for "Mail"
5. Copy the 16-character password and use it in the config

### Step 2: Start Kafka

Run the batch script:
```bash
start-kafka.bat
```

This will start:
- ✅ Zookeeper on port 2181
- ✅ Kafka on port 9092
- ✅ Kafka UI on http://localhost:8080

**Verify Kafka is Running:**
- Open http://localhost:8080 in your browser
- You should see the Kafka UI dashboard

### Step 3: Install Maven Dependencies

```bash
mvn clean install
```

This will download:
- Spring Kafka
- Spring Mail
- All other dependencies

### Step 4: Start the Spring Boot Application

```bash
mvn spring-boot:run
```

Or run from your IDE.

## 📨 How It Works

### 1. **Order Created**
When a customer places an order:
- Order is saved to database
- Kafka producer sends notification to `order-notifications` topic
- Kafka consumer receives the message
- Email service sends confirmation email to customer

**Email includes:**
- Order ID and date
- Order status and payment status
- All order items with quantities and prices
- Shipping address
- Total amount with breakdown (subtotal, shipping, tax)

### 2. **Order Status Updates**
When order status changes (CONFIRMED → SHIPPED → DELIVERED):
- Producer sends to `order-status-updates` topic
- Consumer triggers email notification
- Customer receives update email with new status

### 3. **Payment Completed**
When payment is confirmed:
- Producer sends to `payment-notifications` topic
- Consumer sends payment confirmation email
- Order status automatically updates to CONFIRMED

### 4. **Order Cancelled**
When order is cancelled:
- Producer sends cancellation notification
- Consumer sends cancellation email
- Product stock is restored

## 🎨 Email Notification Types

| Notification Type | Trigger | Email Subject |
|------------------|---------|---------------|
| ORDER_CREATED | Order placed | Order Confirmation - Order #{id} |
| ORDER_CONFIRMED | Payment completed | Your Order Has Been Confirmed |
| PAYMENT_COMPLETED | Payment received | Payment Received - Order #{id} |
| ORDER_SHIPPED | Order dispatched | Your Order Has Been Shipped |
| ORDER_DELIVERED | Order delivered | Your Order Has Been Delivered |
| ORDER_CANCELLED | Order cancelled | Order Cancelled - Order #{id} |

## 🧪 Testing the System

### Test 1: Create an Order (Cash on Delivery)

**Request:** POST http://localhost:8082/api/orders
```json
{
  "addressId": 1,
  "paymentMethod": "cod"
}
```

**Expected Result:**
1. ✅ Order created in database
2. ✅ Kafka message sent to `order-notifications` topic
3. ✅ Email sent to user's email address
4. ✅ Console logs show Kafka producer and consumer activity

**Check Console for:**
```
✅ Sent order notification [Type: ORDER_CREATED] for Order ID: 1 to topic: order-notifications
📨 Received order notification from topic: order-notifications
✉️ Email sent successfully to user@example.com for order #1
```

### Test 2: Update Order Status

**Request:** PUT http://localhost:8082/api/admin/orders/1/status
```json
{
  "status": "SHIPPED",
  "trackingNumber": "TRACK123456"
}
```

**Expected Result:**
1. ✅ Status updated in database
2. ✅ Kafka message sent to `order-status-updates` topic
3. ✅ Email sent with tracking number

### Test 3: Payment Completion (PayPal)

When PayPal payment is completed:
```java
orderNotificationProducer.sendPaymentCompletedNotification(order);
```

**Expected Result:**
1. ✅ Payment status updated
2. ✅ Kafka message sent to `payment-notifications` topic
3. ✅ Payment confirmation email sent

## 📊 Monitor Kafka Topics

### Using Kafka UI (Recommended)
1. Open http://localhost:8080
2. Click on "Topics"
3. View messages in:
   - order-notifications
   - order-status-updates
   - payment-notifications

### View Topic Messages:
- Click on any topic
- Go to "Messages" tab
- See all order notification payloads in real-time

## 🔍 Troubleshooting

### Issue: Email Not Sending

**Check:**
1. Gmail App Password is correct
2. Email configuration in application.properties
3. Gmail account has "Less secure app access" or App Password enabled
4. Check console for email errors

**Solution:**
```
❌ Failed to send email to user@example.com for order #1: Authentication failed
```
- Verify your Gmail App Password
- Make sure 2-Factor Authentication is enabled

### Issue: Kafka Connection Failed

**Check:**
```bash
docker ps
```

**Should show:**
- zookeeper (port 2181)
- kafka (port 9092)
- kafka-ui (port 8080)

**Solution:**
```bash
stop-kafka.bat
start-kafka.bat
```

### Issue: No Kafka Messages Received

**Check:**
1. Kafka consumer is running (check console logs)
2. Topics are created (check Kafka UI)
3. Producer is sending messages (check producer logs)

**Verify Topics:**
```
✅ Created topic: order-notifications (3 partitions)
✅ Created topic: order-status-updates (3 partitions)
✅ Created topic: payment-notifications (3 partitions)
```

## 📧 Sample Email Output

```
Subject: Order Confirmation - Order #123

Dear John Doe,

Your order has been successfully placed and is being processed.

Order Details
-------------
Order ID: #123
Order Date: Oct 13, 2025 at 02:30 PM
Order Status: CONFIRMED
Payment Status: CASH_ON_DELIVERY
Payment Method: cod
Shipping Address: 123 Main St, New York, NY, USA - 10001

Order Items
-----------
Fresh Apples
Quantity: 5 × $2.99 = $14.95
Vendor: Green Valley Farms

Organic Milk
Quantity: 2 × $4.50 = $9.00
Vendor: Dairy Fresh

Price Summary
-------------
Subtotal: $23.95
Shipping Fee: $5.00
Tax: $1.92
Total: $30.87

Thank you for shopping with us!
```

## 🔧 Configuration Options

### Customize Kafka Topics

Edit `KafkaConfig.java`:
```java
public static final String ORDER_NOTIFICATION_TOPIC = "your-custom-topic";
```

### Customize Email Templates

The system uses a simple HTML email template. To use Thymeleaf templates:

1. Create file: `src/main/resources/templates/email/order-notification.html`
2. Email service will automatically use it

### Change Consumer Group

Edit `application.properties`:
```properties
spring.kafka.consumer.group-id=your-custom-group
```

## 📈 Performance Considerations

- Kafka topics have 3 partitions for parallel processing
- Consumer group allows horizontal scaling
- Email sending is asynchronous (doesn't block order creation)
- Failed email attempts are logged but don't affect order processing

## 🎯 Key Features

✅ **Real-time notifications** - Instant email delivery via Kafka
✅ **Asynchronous processing** - Order creation doesn't wait for emails
✅ **Fault tolerant** - Failed emails logged, orders still processed
✅ **Detailed emails** - Complete order information in HTML format
✅ **Multiple notification types** - Created, confirmed, shipped, delivered, cancelled
✅ **Monitoring** - Kafka UI for real-time message tracking
✅ **Scalable** - Multiple consumers can process messages in parallel

## 🚦 Order Flow with Notifications

```
1. Customer places order
   ↓
2. OrderService.createOrder()
   ↓
3. Save order to database
   ↓
4. OrderNotificationProducer.sendOrderCreatedNotification()
   ↓
5. Kafka message sent to "order-notifications" topic
   ↓
6. OrderNotificationConsumer receives message
   ↓
7. EmailNotificationService.sendOrderNotificationEmail()
   ↓
8. Email sent to customer's email address
   ↓
9. Customer receives order confirmation email ✉️
```

## 📞 Support

If you encounter any issues:
1. Check console logs for error messages
2. Verify Kafka UI shows topics and messages
3. Test email configuration with a simple email first
4. Ensure all Docker containers are running

## 🎉 Success Indicators

When everything is working correctly, you should see:

```
✅ Kafka services started
✅ Spring Boot application running on port 8082
✅ Topics created: order-notifications, order-status-updates, payment-notifications
✅ Producer sending messages
✅ Consumer receiving messages
✅ Emails being sent to users
```

Happy coding! 🚀
@echo off
echo Starting Kafka and Zookeeper with Docker Compose...
echo.
echo This will start:
echo - Zookeeper on port 2181
echo - Kafka on port 9092
echo - Kafka UI on port 8080
echo.

docker-compose -f docker-compose-kafka.yml up -d

echo.
echo ✅ Kafka services started successfully!
echo.
echo 📊 Access Kafka UI at: http://localhost:8080
echo 🔗 Kafka Bootstrap Server: localhost:9092
echo.
echo To stop Kafka, run: stop-kafka.bat
pause

