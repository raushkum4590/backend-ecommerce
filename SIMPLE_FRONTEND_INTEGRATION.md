# 🚀 SIMPLEST PayPal Integration - Copy & Paste Ready!

## ✅ Backend Fixed!

Your endpoint now accepts shipping address in the request:
- ✅ Pass `shippingAddress` object in request body
- ✅ Backend validates and saves it automatically
- ✅ No need to pre-save addresses!
- ✅ Creates order from cart automatically
- ✅ Returns PayPal approval URL

## 📱 Ultra-Simple Frontend Code

### Option 1: Vanilla JavaScript (With Shipping Address)

```html
<!-- Add this button anywhere in your HTML -->
<button id="paypal-btn" onclick="payWithPayPal()" style="
  background: #0070BA;
  color: white;
  padding: 15px 30px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
">
  💳 Pay with PayPal
</button>

<script>
async function payWithPayPal() {
  const btn = document.getElementById('paypal-btn');
  btn.disabled = true;
  btn.textContent = '⏳ Processing...';

  try {
    const token = localStorage.getItem('token');
    
    const response = await fetch('http://localhost:8082/api/payment/create', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify({
        shippingAddress: {
          street: "123 Main St",
          city: "New York",
          state: "NY",
          zipCode: "10001",
          country: "USA",
          phoneNumber: "555-1234"
        },
        currency: "USD",
        returnUrl: `${window.location.origin}/payment/success`,
        cancelUrl: `${window.location.origin}/payment/cancel`
      })
    });

    const data = await response.json();

    if (response.ok) {
      // Save for later
      sessionStorage.setItem('orderId', data.orderId);
      sessionStorage.setItem('paypalOrderId', data.paypalOrderId);
      
      // Redirect to PayPal
      window.location.href = data.approvalUrl;
    } else {
      alert('Error: ' + (data.error || 'Payment failed'));
      btn.disabled = false;
      btn.textContent = '💳 Pay with PayPal';
    }
  } catch (error) {
    console.error('Payment error:', error);
    alert('Payment failed: ' + error.message);
    btn.disabled = false;
    btn.textContent = '💳 Pay with PayPal';
  }
}
</script>
```

### Option 2: React Component (With Address Form)

```jsx
// PayPalCheckout.jsx - Complete checkout with address form
import { useState } from 'react';

export default function PayPalCheckout() {
  const [loading, setLoading] = useState(false);
  const [address, setAddress] = useState({
    street: '',
    city: '',
    state: '',
    zipCode: '',
    country: 'USA',
    phoneNumber: ''
  });

  const handleInputChange = (e) => {
    setAddress({
      ...address,
      [e.target.name]: e.target.value
    });
  };

  const handlePayment = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      const token = localStorage.getItem('token');
      
      const response = await fetch('http://localhost:8082/api/payment/create', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({
          shippingAddress: address,
          currency: 'USD',
          returnUrl: `${window.location.origin}/payment/success`,
          cancelUrl: `${window.location.origin}/payment/cancel`
        })
      });

      const data = await response.json();

      if (response.ok) {
        sessionStorage.setItem('orderId', data.orderId);
        sessionStorage.setItem('paypalOrderId', data.paypalOrderId);
        window.location.href = data.approvalUrl;
      } else {
        alert('Error: ' + (data.error || 'Payment failed'));
        setLoading(false);
      }
    } catch (error) {
      console.error('Payment error:', error);
      alert('Payment failed: ' + error.message);
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handlePayment} style={{ maxWidth: '500px', margin: '0 auto' }}>
      <h2>Shipping Address</h2>
      
      <input
        type="text"
        name="street"
        placeholder="Street Address"
        value={address.street}
        onChange={handleInputChange}
        required
        style={{ width: '100%', padding: '10px', marginBottom: '10px' }}
      />
      
      <input
        type="text"
        name="city"
        placeholder="City"
        value={address.city}
        onChange={handleInputChange}
        required
        style={{ width: '100%', padding: '10px', marginBottom: '10px' }}
      />
      
      <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
        <input
          type="text"
          name="state"
          placeholder="State"
          value={address.state}
          onChange={handleInputChange}
          required
          style={{ flex: 1, padding: '10px' }}
        />
        
        <input
          type="text"
          name="zipCode"
          placeholder="Zip Code"
          value={address.zipCode}
          onChange={handleInputChange}
          required
          style={{ flex: 1, padding: '10px' }}
        />
      </div>
      
      <input
        type="text"
        name="country"
        placeholder="Country"
        value={address.country}
        onChange={handleInputChange}
        style={{ width: '100%', padding: '10px', marginBottom: '10px' }}
      />
      
      <input
        type="tel"
        name="phoneNumber"
        placeholder="Phone Number"
        value={address.phoneNumber}
        onChange={handleInputChange}
        style={{ width: '100%', padding: '10px', marginBottom: '20px' }}
      />
      
      <button
        type="submit"
        disabled={loading}
        style={{
          background: '#0070BA',
          color: 'white',
          padding: '15px 30px',
          border: 'none',
          borderRadius: '8px',
          fontSize: '16px',
          cursor: loading ? 'not-allowed' : 'pointer',
          opacity: loading ? 0.6 : 1,
          width: '100%',
        }}
      >
        {loading ? '⏳ Processing...' : '💳 Pay with PayPal'}
      </button>
    </form>
  );
}
```

### Option 3: Simple Button (No Address Form - Uses Saved Address)

```jsx
// SimplePayPalButton.jsx
import { useState } from 'react';

export default function SimplePayPalButton() {
  const [loading, setLoading] = useState(false);

  const handlePayment = async () => {
    setLoading(true);
    
    try {
      const token = localStorage.getItem('token');
      
      const response = await fetch('http://localhost:8082/api/payment/create', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({}) // Empty - uses first saved address
      });

      const data = await response.json();

      if (response.ok) {
        sessionStorage.setItem('orderId', data.orderId);
        sessionStorage.setItem('paypalOrderId', data.paypalOrderId);
        window.location.href = data.approvalUrl;
      } else {
        alert('Error: ' + (data.error || 'Payment failed'));
        setLoading(false);
      }
    } catch (error) {
      console.error('Payment error:', error);
      alert('Payment failed: ' + error.message);
      setLoading(false);
    }
  };

  return (
    <button
      onClick={handlePayment}
      disabled={loading}
      style={{
        background: '#0070BA',
        color: 'white',
        padding: '15px 30px',
        border: 'none',
        borderRadius: '8px',
        fontSize: '16px',
        cursor: loading ? 'not-allowed' : 'pointer',
        opacity: loading ? 0.6 : 1,
      }}
    >
      {loading ? '⏳ Processing...' : '💳 Pay with PayPal'}
    </button>
  );
}
```

## 📥 Payment Success Page (No Changes Needed)

```jsx
// app/payment/success/page.jsx (Next.js 13+)
// OR pages/payment/success.jsx (Next.js 12)

'use client'; // Only for Next.js 13+

import { useEffect, useState } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';

export default function PaymentSuccess() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const [message, setMessage] = useState('Processing payment...');

  useEffect(() => {
    const capture = async () => {
      try {
        const token = localStorage.getItem('token');
        const orderId = sessionStorage.getItem('orderId');
        const paypalOrderId = sessionStorage.getItem('paypalOrderId');

        const response = await fetch('http://localhost:8082/api/payment/capture', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
          },
          body: JSON.stringify({
            orderId: parseInt(orderId),
            paypalOrderId: paypalOrderId,
          }),
        });

        if (response.ok) {
          setMessage('✅ Payment successful!');
          sessionStorage.clear();
          setTimeout(() => router.push('/'), 3000);
        } else {
          setMessage('❌ Payment failed');
        }
      } catch (error) {
        setMessage('❌ Error: ' + error.message);
      }
    };

    capture();
  }, []);

  return (
    <div style={{ textAlign: 'center', padding: '50px' }}>
      <h1>{message}</h1>
    </div>
  );
}
```

## 🎯 Backend Validates:

✅ Street address (required)
✅ City (required)
✅ State (required)  
✅ Zip code (required)
✅ Country (defaults to "USA")
✅ Phone number (optional)

## 📋 Minimum Requirements:

Before clicking "Pay with PayPal", user must have:
1. ✅ Valid JWT token (logged in)
2. ✅ Items in cart
3. ✅ Shipping address in request OR at least one saved address

## 🎉 That's It!

Your backend now accepts shipping address directly in the payment request - no need to save addresses separately!
