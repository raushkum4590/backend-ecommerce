# 🚀 QUICK START - Kafka Email Notifications

## ⚡ Fast Setup (5 minutes)

### 1. Configure Your Email (REQUIRED)

Open `src/main/resources/application.properties` and update:

```properties
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD
```

**Get Gmail App Password:**
- Go to: https://myaccount.google.com/security
- Enable 2-Step Verification
- Search "App Passwords" → Generate new → Copy the 16-char password

### 2. Start Everything

Run this command:
```bash
quick-start.bat
```

This will:
1. ✅ Start Kafka & Zookeeper
2. ✅ Build the application
3. ✅ Start Spring Boot server

### 3. Test It!

#### Option A: Create Order via Frontend
1. Go to http://localhost:3000 (your Next.js app)
2. Add items to cart
3. Checkout with Cash on Delivery
4. ✉️ Check your email!

#### Option B: Test via Postman/API

**POST** `http://localhost:8082/api/orders`

Headers:
```
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

Body:
```json
{
  "addressId": 1,
  "paymentMethod": "cod"
}
```

### 4. Monitor Real-Time

**Console Output:**
```
✅ Sent order notification [Type: ORDER_CREATED] for Order ID: 1
📨 Received order notification from topic: order-notifications
✉️ Email sent successfully to user@example.com for order #1
```

**Kafka UI:**
- Open: http://localhost:8080
- View messages in real-time
- See all notification payloads

### 5. Check Your Email Inbox

You should receive an email with:
- ✅ Order confirmation
- ✅ Order details (ID, date, status)
- ✅ All items ordered
- ✅ Shipping address
- ✅ Price breakdown
- ✅ Estimated delivery date

## 📊 What Happens Behind the Scenes

```
Order Created → Kafka Producer → order-notifications topic
                                        ↓
                                 Kafka Consumer
                                        ↓
                                 Email Service
                                        ↓
                                 📧 User Email
```

## 🎯 Notification Types

| Action | Email Sent | Topic |
|--------|-----------|-------|
| Create Order | Order Confirmation | order-notifications |
| Confirm Order | Order Confirmed | order-notifications |
| Complete Payment | Payment Received | payment-notifications |
| Ship Order | Order Shipped + Tracking | order-status-updates |
| Deliver Order | Order Delivered | order-status-updates |
| Cancel Order | Order Cancelled | order-status-updates |

## 🔧 Troubleshooting

### Email Not Sending?
1. Check Gmail App Password (not your regular password!)
2. Enable 2-Factor Auth on Gmail
3. Check console for errors

### Kafka Not Starting?
```bash
docker ps
```
Should show: zookeeper, kafka, kafka-ui

If not running:
```bash
stop-kafka.bat
start-kafka.bat
```

### No Notifications?
Check console for:
```
✅ Created topic: order-notifications
```

If missing, restart the application.

## 📧 Email Configuration for Other Providers

### Outlook/Hotmail
```properties
spring.mail.host=smtp.office365.com
spring.mail.port=587
```

### Yahoo
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
```

### Custom SMTP
```properties
spring.mail.host=smtp.yourprovider.com
spring.mail.port=587
spring.mail.username=your-email@domain.com
spring.mail.password=your-password
```

## ✅ Success Checklist

- [ ] Docker Desktop is running
- [ ] Kafka started (localhost:9092)
- [ ] Email configured in application.properties
- [ ] Spring Boot running (localhost:8082)
- [ ] Created a test order
- [ ] Received email notification

## 🎉 You're Done!

Your Kafka email notification system is now running! Every order action will automatically trigger email notifications to users.

---

**Need Help?** Check the full guide: `KAFKA_EMAIL_NOTIFICATION_GUIDE.md`
@echo off
echo ========================================
echo    KAFKA EMAIL NOTIFICATION SYSTEM
echo    Quick Start Script
echo ========================================
echo.

echo Step 1: Starting Kafka and Zookeeper...
echo.
call start-kafka.bat

echo.
echo Step 2: Waiting for Kafka to be ready (15 seconds)...
timeout /t 15 /nobreak

echo.
echo Step 3: Building the application...
call mvn clean install -DskipTests

echo.
echo Step 4: Starting Spring Boot application...
echo.
echo ✅ Kafka is running on: localhost:9092
echo ✅ Kafka UI available at: http://localhost:8080
echo ✅ Spring Boot will start on: http://localhost:8082
echo.
echo 📧 Email notifications will be sent automatically when:
echo    - Orders are created
echo    - Orders are updated
echo    - Payments are completed
echo    - Orders are shipped/delivered/cancelled
echo.
echo Press Ctrl+C to stop the application
echo.

call mvn spring-boot:run

