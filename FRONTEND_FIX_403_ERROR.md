# 🔴 FIX: 403 Error - Wrong Endpoint Issue

## ❌ Problem
Your frontend is calling: `POST http://localhost:8082/api/payment/create`
But the backend endpoint is: `POST http://localhost:8082/api/payments/create-payment`

## ✅ Solution

### Option 1: Update Frontend (Recommended)
Change your frontend API call to use the correct endpoint:

```javascript
// WRONG ❌
const response = await fetch('http://localhost:8082/api/payment/create', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  },
  body: JSON.stringify({
    amount: 5.99,
    currency: 'USD',
    items: 2
  }),
});

// CORRECT ✅
const response = await fetch('http://localhost:8082/api/payments/create-payment', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  },
  body: JSON.stringify({
    orderId: yourOrderId,  // Required!
    currency: 'USD',
    returnUrl: `${window.location.origin}/payment/success`,
    cancelUrl: `${window.location.origin}/payment/cancel`,
  }),
});
```

### Option 2: Add Alias Endpoint in Backend
If you can't change the frontend immediately, add this method to PaymentController:

```java
/**
 * Alias endpoint for backward compatibility
 * POST /api/payment/create
 */
@PostMapping("/create")
public ResponseEntity<?> createPaymentAlias(@RequestBody PaymentIntentRequest request) {
    return createPayment(request);  // Calls the existing method
}
```

But you'll also need to add a new controller mapping:

```java
@RestController
@RequestMapping("/api/payment")  // Note: singular "payment"
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "false")
public class PaymentAliasController {
    
    private final PaymentController paymentController;
    
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PaymentIntentRequest request) {
        return paymentController.createPayment(request);
    }
}
```

## 🎯 Quick Frontend Fix

If you're using the `lib/api.js` from the integration guide, **you're already good!** Just make sure you're calling:

```javascript
import { paymentAPI } from '@/lib/api';

// Use this function
const response = await paymentAPI.createPayment(orderId);
```

## 📋 Required Request Body

The backend expects:
```json
{
  "orderId": 123,
  "currency": "USD",
  "returnUrl": "http://localhost:3000/payment/success",
  "cancelUrl": "http://localhost:3000/payment/cancel"
}
```

**NOT:**
```json
{
  "amount": 5.99,
  "currency": "USD",
  "items": 2
}
```

## 🔍 Why 403 Error?

The 403 error occurs because:
1. ❌ Wrong endpoint path → Request doesn't match any secured endpoint
2. ❌ Spring Security blocks unmatched endpoints by default
3. ❌ Missing `orderId` in request body

## ✅ Complete Working Example

```javascript
// In your checkout component
const handlePayPalPayment = async () => {
  try {
    // Step 1: First create an order on your backend
    const orderResponse = await fetch('http://localhost:8082/api/orders', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
      },
      body: JSON.stringify({
        // Your order data
        items: [
          { productId: 1, quantity: 2 }
        ]
      }),
    });
    
    const order = await orderResponse.json();
    console.log('Order created:', order);

    // Step 2: Create PayPal payment with the order ID
    const paymentResponse = await fetch('http://localhost:8082/api/payments/create-payment', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
      },
      body: JSON.stringify({
        orderId: order.id,  // Use the order ID from step 1
        currency: 'USD',
        returnUrl: `${window.location.origin}/payment/success`,
        cancelUrl: `${window.location.origin}/payment/cancel`,
      }),
    });

    const paymentData = await paymentResponse.json();
    console.log('PayPal payment created:', paymentData);

    // Step 3: Redirect to PayPal
    if (paymentData.approvalUrl) {
      sessionStorage.setItem('orderId', order.id);
      sessionStorage.setItem('paypalOrderId', paymentData.paypalOrderId);
      window.location.href = paymentData.approvalUrl;
    }

  } catch (error) {
    console.error('❌ Payment error:', error);
    alert('Payment failed: ' + error.message);
  }
};
```

## 🚀 Test Steps

1. **Login first** to get a valid JWT token
2. **Create an order** using `/api/orders` endpoint
3. **Create PayPal payment** using the order ID from step 2
4. **Redirect to PayPal** using the approval URL
5. **Complete payment** on PayPal sandbox
6. **Get redirected back** to your success page
7. **Backend captures payment** automatically

## 📞 Backend Endpoints (Correct)

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/payments/create-payment` | POST | ✅ Required | Create PayPal order |
| `/api/payments/capture` | POST | ✅ Required | Capture payment |
| `/api/payments/status/{id}` | GET | ✅ Required | Get payment status |

---

**TL;DR:** Change your frontend to call `/api/payments/create-payment` instead of `/api/payment/create`

