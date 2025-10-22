# Next.js PayPal Integration Guide

## Backend Setup ✅
Your Spring Boot backend is now configured correctly with:
- Fixed database foreign key constraints
- CORS enabled for frontend access
- JWT token expiration set to 24 hours
- PayPal endpoints ready at `/api/payments`

## Frontend Integration (Next.js)

### 1. Create API Service File

Create `lib/api.js` or `lib/api.ts`:

```javascript
// lib/api.js
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8082/api';

// Get auth token from localStorage/cookies
const getAuthToken = () => {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('token');
  }
  return null;
};

// API request wrapper
export const apiRequest = async (endpoint, options = {}) => {
  const token = getAuthToken();
  
  const headers = {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` }),
    ...options.headers,
  };

  const config = {
    ...options,
    headers,
  };

  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
    
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.error || errorData.message || `HTTP ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('🔴 API Error:', error.message);
    throw error;
  }
};

// PayPal specific functions
export const paymentAPI = {
  // Create PayPal order
  createPayment: async (orderId) => {
    return apiRequest('/payments/create-payment', {
      method: 'POST',
      body: JSON.stringify({
        orderId: orderId,
        currency: 'USD',
        returnUrl: `${window.location.origin}/payment/success`,
        cancelUrl: `${window.location.origin}/payment/cancel`,
      }),
    });
  },

  // Capture payment after PayPal approval
  capturePayment: async (orderId, paypalOrderId) => {
    return apiRequest('/payments/capture', {
      method: 'POST',
      body: JSON.stringify({
        orderId: orderId,
        paypalOrderId: paypalOrderId,
      }),
    });
  },

  // Get payment status
  getPaymentStatus: async (paypalOrderId) => {
    return apiRequest(`/payments/status/${paypalOrderId}`, {
      method: 'GET',
    });
  },
};
```

### 2. Create PayPal Payment Component

Create `components/PayPalCheckout.jsx`:

```javascript
// components/PayPalCheckout.jsx
import { useState } from 'react';
import { paymentAPI } from '@/lib/api';

export default function PayPalCheckout({ orderId, onSuccess, onError }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handlePayPalPayment = async () => {
    try {
      setLoading(true);
      setError(null);

      console.log('🔵 Creating PayPal order for:', orderId);
      
      // Step 1: Create PayPal order on backend
      const paymentResponse = await paymentAPI.createPayment(orderId);
      
      console.log('✅ PayPal order created:', paymentResponse);

      // Step 2: Redirect to PayPal approval URL
      if (paymentResponse.approvalUrl) {
        // Store PayPal order ID for later capture
        sessionStorage.setItem('paypalOrderId', paymentResponse.paypalOrderId);
        sessionStorage.setItem('orderId', orderId);
        
        // Redirect to PayPal
        window.location.href = paymentResponse.approvalUrl;
      } else {
        throw new Error('No approval URL received from server');
      }
      
    } catch (error) {
      console.error('❌ Payment error:', error);
      setError(error.message || 'Failed to create payment');
      if (onError) onError(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="paypal-checkout">
      {error && (
        <div className="error-message" style={{ color: 'red', marginBottom: '1rem' }}>
          ⚠️ {error}
        </div>
      )}
      
      <button
        onClick={handlePayPalPayment}
        disabled={loading}
        className="paypal-button"
        style={{
          backgroundColor: '#0070BA',
          color: 'white',
          padding: '12px 24px',
          border: 'none',
          borderRadius: '4px',
          fontSize: '16px',
          cursor: loading ? 'not-allowed' : 'pointer',
          opacity: loading ? 0.6 : 1,
        }}
      >
        {loading ? '⏳ Processing...' : '💳 Pay with PayPal'}
      </button>
    </div>
  );
}
```

### 3. Create Payment Success Page

Create `app/payment/success/page.jsx` or `pages/payment/success.jsx`:

```javascript
// app/payment/success/page.jsx (Next.js 13+)
'use client';

import { useEffect, useState } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import { paymentAPI } from '@/lib/api';

export default function PaymentSuccess() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const [status, setStatus] = useState('processing');
  const [message, setMessage] = useState('Processing your payment...');

  useEffect(() => {
    const capturePayment = async () => {
      try {
        // Get PayPal order ID from URL params
        const paypalOrderId = searchParams.get('token'); // PayPal returns 'token' param
        const orderId = sessionStorage.getItem('orderId');

        if (!paypalOrderId || !orderId) {
          setStatus('error');
          setMessage('Missing payment information');
          return;
        }

        console.log('🔵 Capturing payment:', { orderId, paypalOrderId });

        // Capture the payment on backend
        const result = await paymentAPI.capturePayment(orderId, paypalOrderId);

        console.log('✅ Payment captured:', result);

        setStatus('success');
        setMessage('Payment successful! Order confirmed.');

        // Clean up session storage
        sessionStorage.removeItem('paypalOrderId');
        sessionStorage.removeItem('orderId');

        // Redirect to order details after 3 seconds
        setTimeout(() => {
          router.push(`/orders/${orderId}`);
        }, 3000);

      } catch (error) {
        console.error('❌ Payment capture error:', error);
        setStatus('error');
        setMessage(error.message || 'Payment failed. Please contact support.');
      }
    };

    capturePayment();
  }, [searchParams, router]);

  return (
    <div style={{ textAlign: 'center', padding: '2rem' }}>
      <h1>Payment {status === 'success' ? '✅' : status === 'error' ? '❌' : '⏳'}</h1>
      <p>{message}</p>
      {status === 'processing' && <div className="spinner">Loading...</div>}
    </div>
  );
}
```

### 4. Create Payment Cancel Page

Create `app/payment/cancel/page.jsx`:

```javascript
// app/payment/cancel/page.jsx
'use client';

import { useRouter } from 'next/navigation';

export default function PaymentCancel() {
  const router = useRouter();

  return (
    <div style={{ textAlign: 'center', padding: '2rem' }}>
      <h1>❌ Payment Cancelled</h1>
      <p>Your payment was cancelled. No charges were made.</p>
      <button 
        onClick={() => router.push('/cart')}
        style={{ marginTop: '1rem', padding: '10px 20px' }}
      >
        Return to Cart
      </button>
    </div>
  );
}
```

### 5. Environment Variables

Create `.env.local`:

```env
NEXT_PUBLIC_API_URL=http://localhost:8082/api
```

### 6. Usage Example in Checkout Page

```javascript
// app/checkout/page.jsx
'use client';

import { useState, useEffect } from 'react';
import PayPalCheckout from '@/components/PayPalCheckout';

export default function CheckoutPage() {
  const [orderId, setOrderId] = useState(null);

  // Create order when page loads
  useEffect(() => {
    // Call your order creation API
    const createOrder = async () => {
      // Your order creation logic here
      const response = await fetch('http://localhost:8082/api/orders', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({
          // your order data
        }),
      });
      const data = await response.json();
      setOrderId(data.id);
    };
    
    createOrder();
  }, []);

  return (
    <div>
      <h1>Checkout</h1>
      {orderId && (
        <PayPalCheckout
          orderId={orderId}
          onSuccess={() => console.log('Payment initiated')}
          onError={(error) => console.error('Payment failed:', error)}
        />
      )}
    </div>
  );
}
```

## Troubleshooting

### 403 Forbidden Error
✅ **FIXED**: Database cleaned, CORS configured, JWT tokens now expire in 24 hours

### Common Issues:

1. **Token Expired**: Login again to get a new token
2. **CORS Error**: Backend now allows all origins with credentials
3. **Order Not Found**: Make sure the order exists and belongs to the logged-in user
4. **PayPal Redirect Fails**: Check console for approval URL

## Backend Endpoints Summary

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/payments/create-payment` | POST | Create PayPal order |
| `/api/payments/capture` | POST | Capture payment after approval |
| `/api/payments/status/{id}` | GET | Get payment status |

## Testing

1. Login to get JWT token
2. Create an order
3. Click "Pay with PayPal"
4. Complete payment on PayPal sandbox
5. Get redirected back to success page
6. Payment automatically captured

🎉 Your integration is now ready!

