// components/PayPalCheckout.jsx
'use client';

import { useState } from 'react';

export default function PayPalCheckout() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [address, setAddress] = useState({
    street: '',
    city: '',
    state: '',
    zipCode: '',
    country: 'USA',
    phoneNumber: ''
  });

  const handleChange = (e) => {
    setAddress({
      ...address,
      [e.target.name]: e.target.value
    });
  };

  const handlePayment = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const token = localStorage.getItem('token');

      console.log('=== FRONTEND DEBUG START ===');
      console.log('🔑 Token exists:', !!token);
      console.log('🔑 Token preview:', token ? `${token.substring(0, 30)}...` : 'null');

      if (!token) {
        throw new Error('Please login first. No authentication token found.');
      }

      const requestBody = {
        shippingAddress: address,
        currency: 'USD',
        returnUrl: `${window.location.origin}/payment/success`,
        cancelUrl: `${window.location.origin}/payment/cancel`
      };

      console.log('📤 Request URL:', 'http://localhost:8082/api/payment/create');
      console.log('📤 Request body:', JSON.stringify(requestBody, null, 2));

      const response = await fetch('http://localhost:8082/api/payment/create', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(requestBody)
      });

      console.log('📥 Response Status:', response.status, response.statusText);
      console.log('📥 Response OK:', response.ok);
      console.log('📥 Response Headers:', Object.fromEntries(response.headers.entries()));

      // Get raw response text FIRST
      const responseText = await response.text();
      console.log('📥 Raw Response Text:', responseText);
      console.log('📥 Response Length:', responseText.length, 'characters');

      // Try to parse JSON
      let data = {};
      if (responseText && responseText.trim()) {
        try {
          data = JSON.parse(responseText);
          console.log('📦 Parsed JSON Data:', data);
          console.log('📦 Data Keys:', Object.keys(data));
        } catch (parseError) {
          console.error('❌ JSON Parse Failed:', parseError.message);
          console.error('❌ Raw Text:', responseText.substring(0, 500));
          throw new Error(`Server returned invalid JSON. Status: ${response.status}. Response: ${responseText.substring(0, 200)}`);
        }
      } else {
        console.warn('⚠️ Empty response body received');
        data = {};
      }

      if (response.ok && response.status === 200) {
        console.log('✅ Status 200 - Success response');

        // Check required fields
        const hasOrderId = !!data.orderId;
        const hasPaypalOrderId = !!data.paypalOrderId;
        const hasApprovalUrl = !!data.approvalUrl;

        console.log('✅ Has orderId:', hasOrderId, data.orderId);
        console.log('✅ Has paypalOrderId:', hasPaypalOrderId, data.paypalOrderId);
        console.log('✅ Has approvalUrl:', hasApprovalUrl, data.approvalUrl);

        if (!hasOrderId || !hasPaypalOrderId || !hasApprovalUrl) {
          console.error('❌ Missing required fields in success response');
          throw new Error(`Server returned success but missing data. Received: ${JSON.stringify(data)}`);
        }

        console.log('💾 Saving to sessionStorage...');
        sessionStorage.setItem('orderId', data.orderId.toString());
        sessionStorage.setItem('paypalOrderId', data.paypalOrderId);

        console.log('🔄 Redirecting to PayPal:', data.approvalUrl);
        console.log('=== FRONTEND DEBUG END (SUCCESS) ===');

        window.location.href = data.approvalUrl;
      } else {
        // Error response
        console.error('❌ Error Response - Status:', response.status);
        console.error('❌ Response Data:', data);

        let errorMessage = 'Payment creation failed';

        // Try different error field names
        if (data.error) {
          errorMessage = data.error;
          console.error('❌ data.error:', data.error);
        } else if (data.message) {
          errorMessage = data.message;
          console.error('❌ data.message:', data.message);
        } else if (data.details) {
          errorMessage = data.details;
          console.error('❌ data.details:', data.details);
        }

        // Add more context based on status code
        if (response.status === 401) {
          errorMessage = '🔐 Unauthorized: Your session has expired. Please login again.';
          console.error('❌ 401: Token is invalid or expired');
        } else if (response.status === 403) {
          errorMessage = '🚫 Forbidden: Access denied. Please login again.';
          console.error('❌ 403: Forbidden - Check backend logs for details');
        } else if (response.status === 400) {
          if (!errorMessage || errorMessage === 'Payment creation failed') {
            errorMessage = '⚠️ Bad Request: ' + (data.error || data.details || 'Check if cart has items and all fields are filled');
          }
          console.error('❌ 400: Bad Request -', errorMessage);
        } else if (response.status === 500) {
          errorMessage = '💥 Server Error: Please check backend logs and try again.';
          console.error('❌ 500: Internal Server Error');
        }

        console.error('=== FRONTEND DEBUG END (ERROR) ===');
        throw new Error(errorMessage);
      }
    } catch (err) {
      console.error('=== EXCEPTION CAUGHT ===');
      console.error('❌ Error Type:', err.constructor.name);
      console.error('❌ Error Message:', err.message);
      console.error('❌ Error Stack:', err.stack);

      let userMessage = err.message;

      // Network errors
      if (err.message.includes('Failed to fetch') || err.name === 'TypeError') {
        userMessage = '🔌 Cannot connect to backend server.\n\nPlease check:\n• Is backend running on http://localhost:8082?\n• Check browser console for CORS errors\n• Try accessing http://localhost:8082/api/products directly';
        console.error('❌ Network Error: Backend may not be running or CORS issue');
      }

      console.error('=== ERROR END ===');
      setError(userMessage);
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto', padding: '2rem' }}>
      <h2>Complete Your Purchase</h2>

      {error && (
        <div style={{
          background: '#fee',
          border: '1px solid #fcc',
          padding: '1rem',
          borderRadius: '8px',
          marginBottom: '1rem',
          color: '#c00',
          whiteSpace: 'pre-line'
        }}>
          <strong>⚠️ Error:</strong>
          <p style={{ margin: '0.5rem 0 0 0' }}>{error}</p>
        </div>
      )}

      <form onSubmit={handlePayment}>
        <h3>Shipping Address</h3>

        <div style={{ marginBottom: '1rem' }}>
          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 'bold' }}>
            Street Address *
          </label>
          <input
            type="text"
            name="street"
            value={address.street}
            onChange={handleChange}
            required
            placeholder="123 Main Street"
            style={{
              width: '100%',
              padding: '12px',
              border: '1px solid #ddd',
              borderRadius: '4px',
              fontSize: '16px'
            }}
          />
        </div>

        <div style={{ marginBottom: '1rem' }}>
          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 'bold' }}>
            City *
          </label>
          <input
            type="text"
            name="city"
            value={address.city}
            onChange={handleChange}
            required
            placeholder="New York"
            style={{
              width: '100%',
              padding: '12px',
              border: '1px solid #ddd',
              borderRadius: '4px',
              fontSize: '16px'
            }}
          />
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 'bold' }}>
              State *
            </label>
            <input
              type="text"
              name="state"
              value={address.state}
              onChange={handleChange}
              required
              placeholder="NY"
              style={{
                width: '100%',
                padding: '12px',
                border: '1px solid #ddd',
                borderRadius: '4px',
                fontSize: '16px'
              }}
            />
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 'bold' }}>
              Zip Code *
            </label>
            <input
              type="text"
              name="zipCode"
              value={address.zipCode}
              onChange={handleChange}
              required
              placeholder="10001"
              style={{
                width: '100%',
                padding: '12px',
                border: '1px solid #ddd',
                borderRadius: '4px',
                fontSize: '16px'
              }}
            />
          </div>
        </div>

        <div style={{ marginBottom: '1rem' }}>
          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 'bold' }}>
            Country
          </label>
          <input
            type="text"
            name="country"
            value={address.country}
            onChange={handleChange}
            placeholder="USA"
            style={{
              width: '100%',
              padding: '12px',
              border: '1px solid #ddd',
              borderRadius: '4px',
              fontSize: '16px'
            }}
          />
        </div>

        <div style={{ marginBottom: '1.5rem' }}>
          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 'bold' }}>
            Phone Number
          </label>
          <input
            type="tel"
            name="phoneNumber"
            value={address.phoneNumber}
            onChange={handleChange}
            placeholder="555-1234"
            style={{
              width: '100%',
              padding: '12px',
              border: '1px solid #ddd',
              borderRadius: '4px',
              fontSize: '16px'
            }}
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          style={{
            width: '100%',
            padding: '16px',
            background: loading ? '#999' : '#0070BA',
            color: 'white',
            border: 'none',
            borderRadius: '8px',
            fontSize: '18px',
            fontWeight: 'bold',
            cursor: loading ? 'not-allowed' : 'pointer',
            transition: 'background 0.2s',
          }}
          onMouseOver={(e) => !loading && (e.target.style.background = '#005ea6')}
          onMouseOut={(e) => !loading && (e.target.style.background = '#0070BA')}
        >
          {loading ? (
            <>⏳ Processing...</>
          ) : (
            <>💳 Pay with PayPal</>
          )}
        </button>
      </form>

      <div style={{
        marginTop: '1rem',
        padding: '1rem',
        background: '#f0f8ff',
        borderRadius: '8px',
        fontSize: '14px',
        color: '#666'
      }}>
        🔒 Secure payment powered by PayPal Sandbox
        <br />
        <small>⚠️ <strong>Open browser console (F12)</strong> to see detailed debugging info</small>
      </div>
    </div>
  );
}

