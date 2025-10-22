# PayPal Payment Integration Guide

## Overview
This application now supports PayPal Payment API for processing payments securely. PayPal handles card payments, PayPal balance, and other payment methods with buyer protection.

## Configuration

### 1. Update application.properties
Replace the placeholder with your actual PayPal credentials:
```properties
paypal.client.id=your_paypal_client_id_here
paypal.client.secret=your_paypal_client_secret_here
paypal.mode=sandbox
```

**Important:** 
- Use `sandbox` mode for development/testing
- Use `live` mode for production
- Get credentials from [PayPal Developer Dashboard](https://developer.paypal.com/dashboard/)
- Never commit your secret keys to version control

### 2. Getting PayPal API Credentials

1. Go to [PayPal Developer Dashboard](https://developer.paypal.com/dashboard/)
2. Login with your PayPal account
3. Go to "My Apps & Credentials"
4. Create a new app or use an existing one
5. Copy the **Client ID** and **Secret**
6. For testing, use Sandbox credentials
7. For production, switch to Live credentials

## API Endpoints

### 1. Create PayPal Payment
**Endpoint:** `POST /api/payments/create-payment`

**Description:** Creates a PayPal order for payment. Returns an approval URL where the user will complete payment on PayPal's secure site.

**Headers:**
```
Authorization: Bearer <your_jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "orderId": 1,
  "currency": "USD",
  "returnUrl": "http://localhost:3000/payment-success",
  "cancelUrl": "http://localhost:3000/payment-cancel"
}
```

**Response (Success - 200):**
```json
{
  "approvalUrl": "https://www.sandbox.paypal.com/checkoutnow?token=xxxxx",
  "paypalOrderId": "5O190127TN364715T",
  "amount": 25.99,
  "currency": "USD",
  "status": "CREATED"
}
```

**Response (Error - 400/403):**
```json
{
  "error": "Error message"
}
```

### 2. Capture Payment
**Endpoint:** `POST /api/payments/capture`

**Description:** Captures the payment after user approves it on PayPal. Call this endpoint when user returns from PayPal after successful authorization.

**Headers:**
```
Authorization: Bearer <your_jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "orderId": 1,
  "paypalOrderId": "5O190127TN364715T"
}
```

**Response (Success - 200):**
```json
{
  "success": true,
  "message": "Payment captured successfully",
  "order": {
    "id": 1,
    "paymentStatus": "COMPLETED",
    "orderStatus": "PENDING",
    "totalAmount": 25.99,
    ...
  }
}
```

### 3. Get Payment Status
**Endpoint:** `GET /api/payments/status/{paypalOrderId}`

**Description:** Check the current status of a PayPal order.

**Headers:**
```
Authorization: Bearer <your_jwt_token>
```

**Response (Success - 200):**
```json
{
  "paypalOrderId": "5O190127TN364715T",
  "status": "COMPLETED"
}
```

**Possible Statuses:**
- `CREATED`: Order created, awaiting buyer approval
- `SAVED`: Buyer saved the order but hasn't completed payment
- `APPROVED`: Buyer approved the payment
- `VOIDED`: Order was voided
- `COMPLETED`: Payment captured successfully

## Payment Flow

### Complete Payment Process:

1. **Create an Order**
   ```
   POST /api/orders
   Body: {
     "addressId": 1,
     "paymentMethod": "paypal"
   }
   ```
   Response includes orderId

2. **Create PayPal Payment**
   ```
   POST /api/payments/create-payment
   Body: {
     "orderId": 1,
     "currency": "USD",
     "returnUrl": "http://yoursite.com/success",
     "cancelUrl": "http://yoursite.com/cancel"
   }
   ```
   Response includes `approvalUrl`

3. **Redirect User to PayPal**
   - Redirect user to the `approvalUrl`
   - User logs into PayPal and approves payment
   - PayPal redirects back to your `returnUrl`

4. **Capture Payment**
   ```
   POST /api/payments/capture
   Body: {
     "orderId": 1,
     "paypalOrderId": "5O190127TN364715T"
   }
   ```

5. **Order status updates to COMPLETED**

## Frontend Integration Example

### React/JavaScript Example:

```javascript
import React, { useState } from 'react';

function PaymentComponent({ orderId }) {
    const [loading, setLoading] = useState(false);

    const handlePayment = async () => {
        setLoading(true);
        
        try {
            // Step 1: Create PayPal payment
            const response = await fetch('/api/payments/create-payment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                },
                body: JSON.stringify({
                    orderId: orderId,
                    currency: 'USD',
                    returnUrl: window.location.origin + '/payment-success?orderId=' + orderId,
                    cancelUrl: window.location.origin + '/payment-cancel'
                })
            });

            const data = await response.json();
            
            if (data.approvalUrl) {
                // Step 2: Redirect to PayPal
                window.location.href = data.approvalUrl;
            } else {
                alert('Error creating payment: ' + data.error);
            }
        } catch (error) {
            console.error('Payment error:', error);
            alert('Payment failed');
        } finally {
            setLoading(false);
        }
    };

    return (
        <button onClick={handlePayment} disabled={loading}>
            {loading ? 'Processing...' : 'Pay with PayPal'}
        </button>
    );
}

// Payment Success Page Component
function PaymentSuccess() {
    const [captured, setCaptured] = useState(false);
    
    useEffect(() => {
        const capturePayment = async () => {
            const urlParams = new URLSearchParams(window.location.search);
            const orderId = urlParams.get('orderId');
            const token = urlParams.get('token'); // PayPal order ID
            
            try {
                const response = await fetch('/api/payments/capture', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                    },
                    body: JSON.stringify({
                        orderId: orderId,
                        paypalOrderId: token
                    })
                });

                const data = await response.json();
                
                if (data.success) {
                    setCaptured(true);
                    alert('Payment successful!');
                } else {
                    alert('Payment capture failed');
                }
            } catch (error) {
                console.error('Capture error:', error);
            }
        };

        capturePayment();
    }, []);

    return (
        <div>
            {captured ? (
                <h1>Payment Successful! Thank you for your purchase.</h1>
            ) : (
                <h1>Processing payment...</h1>
            )}
        </div>
    );
}

export { PaymentComponent, PaymentSuccess };
```

### Simple HTML + JavaScript Example:

```html
<!DOCTYPE html>
<html>
<head>
    <title>PayPal Payment</title>
</head>
<body>
    <h1>Complete Your Payment</h1>
    <button id="paypal-button">Pay with PayPal</button>
    <div id="message"></div>

    <script>
        document.getElementById('paypal-button').addEventListener('click', async () => {
            const orderId = 1; // Get from your order
            
            try {
                // Create PayPal payment
                const response = await fetch('/api/payments/create-payment', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                    },
                    body: JSON.stringify({
                        orderId: orderId,
                        currency: 'USD',
                        returnUrl: window.location.origin + '/payment-success.html?orderId=' + orderId,
                        cancelUrl: window.location.origin + '/payment-cancel.html'
                    })
                });

                const data = await response.json();
                
                if (data.approvalUrl) {
                    // Redirect to PayPal
                    window.location.href = data.approvalUrl;
                } else {
                    document.getElementById('message').textContent = 'Error: ' + data.error;
                }
            } catch (error) {
                document.getElementById('message').textContent = 'Payment failed';
            }
        });
    </script>
</body>
</html>
```

### Payment Success Page (payment-success.html):

```html
<!DOCTYPE html>
<html>
<head>
    <title>Payment Success</title>
</head>
<body>
    <h1 id="status">Processing payment...</h1>

    <script>
        async function capturePayment() {
            const urlParams = new URLSearchParams(window.location.search);
            const orderId = urlParams.get('orderId');
            const paypalOrderId = urlParams.get('token');
            
            try {
                const response = await fetch('/api/payments/capture', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                    },
                    body: JSON.stringify({
                        orderId: orderId,
                        paypalOrderId: paypalOrderId
                    })
                });

                const data = await response.json();
                
                if (data.success) {
                    document.getElementById('status').textContent = 
                        'Payment Successful! Thank you for your purchase.';
                } else {
                    document.getElementById('status').textContent = 
                        'Payment capture failed: ' + data.error;
                }
            } catch (error) {
                document.getElementById('status').textContent = 
                    'Error processing payment';
            }
        }

        capturePayment();
    </script>
</body>
</html>
```

## Testing

### Sandbox Testing

1. Use `paypal.mode=sandbox` in application.properties
2. Get sandbox credentials from PayPal Developer Dashboard
3. Use PayPal sandbox test accounts for testing

### PayPal Test Accounts

In sandbox mode, you can create test buyer and seller accounts:
1. Go to [PayPal Sandbox Accounts](https://developer.paypal.com/dashboard/accounts)
2. Create a test buyer account
3. Use these credentials when redirected to PayPal for payment

**Test Buyer Account:**
- You can create multiple test accounts with different scenarios
- Each account has test PayPal balance
- No real money is charged in sandbox mode

## Security Best Practices

1. **Never expose your Client Secret** - Only use it server-side
2. **Use HTTPS** in production
3. **Validate all payment captures** on the backend
4. **Store minimal payment data** - Let PayPal handle sensitive info
5. **Implement proper error handling**
6. **Use environment variables** for API credentials
7. **Always verify payment status** before fulfilling orders

## Currency Support

PayPal supports 25+ currencies including:
- `USD` - US Dollar
- `EUR` - Euro
- `GBP` - British Pound
- `CAD` - Canadian Dollar
- `INR` - Indian Rupee
- `AUD` - Australian Dollar
- `JPY` - Japanese Yen

**Note:** Currency must be supported by both buyer and seller accounts.

## Error Handling

Common errors:
- `Order not found` - Invalid orderId
- `You don't have permission` - Order doesn't belong to user
- `PayPal error` - Payment processing failed
- `Payment capture failed` - Payment wasn't approved or already captured

## Refunds

To refund a payment (typically on order cancellation), implement refund logic using the capture ID from PayPal order details.

## Advantages of PayPal

✅ **Buyer Protection** - Customers trust PayPal  
✅ **No Card Details** - Don't handle sensitive card data  
✅ **Multiple Payment Methods** - PayPal balance, cards, bank accounts  
✅ **Global Support** - Available in 200+ countries  
✅ **Easy Integration** - Simple redirect flow  
✅ **Mobile Optimized** - Works seamlessly on mobile devices

## Migration from Stripe

If you're migrating from Stripe:
1. Update `application.properties` with PayPal credentials
2. Use `/api/payments/create-payment` instead of `/api/payments/create-payment-intent`
3. Redirect users to `approvalUrl` instead of using Stripe.js
4. Capture payment with `/api/payments/capture` after user returns from PayPal
5. PayPal order ID is stored in `paypalOrderId` field instead of `stripePaymentIntentId`

## Additional Resources

- [PayPal Developer Documentation](https://developer.paypal.com/docs/)
- [PayPal REST API Reference](https://developer.paypal.com/docs/api/orders/v2/)
- [PayPal Sandbox Testing](https://developer.paypal.com/docs/api-basics/sandbox/)
- [PayPal Integration Guide](https://developer.paypal.com/docs/checkout/)
