package com.example.demo.service;

import com.example.demo.dto.PaymentIntentResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.PaymentStatus;
import com.example.demo.repository.OrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PayPalPaymentService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getBaseUrl() {
        return "sandbox".equalsIgnoreCase(mode)
            ? "https://api-m.sandbox.paypal.com"
            : "https://api-m.paypal.com";
    }

    /**
     * Get PayPal access token
     */
    private String getAccessToken() {
        try {
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + encodedAuth);

            String body = "grant_type=client_credentials";
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/v1/oauth2/token",
                request,
                String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get PayPal access token: " + e.getMessage(), e);
        }
    }

    /**
     * Create a PayPal Order for payment
     */
    public PaymentIntentResponse createPayPalOrder(Order order, String currency, String returnUrl, String cancelUrl) {
        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            // Build order request JSON
            Map<String, Object> orderRequest = new HashMap<>();
            orderRequest.put("intent", "CAPTURE");

            // Application context
            Map<String, String> appContext = new HashMap<>();
            appContext.put("return_url", returnUrl);
            appContext.put("cancel_url", cancelUrl);
            appContext.put("brand_name", "Grocery Store");
            appContext.put("landing_page", "BILLING");
            appContext.put("shipping_preference", "NO_SHIPPING");
            orderRequest.put("application_context", appContext);

            // Purchase units
            List<Map<String, Object>> purchaseUnits = new ArrayList<>();
            Map<String, Object> purchaseUnit = new HashMap<>();
            purchaseUnit.put("reference_id", order.getId().toString());
            purchaseUnit.put("description", "Order #" + order.getId() + " - Grocery Purchase");
            purchaseUnit.put("custom_id", order.getId().toString());

            // Amount breakdown
            Map<String, Object> amount = new HashMap<>();
            amount.put("currency_code", currency);
            amount.put("value", String.format("%.2f", order.getTotalAmount()));

            Map<String, Object> breakdown = new HashMap<>();
            Map<String, String> itemTotal = new HashMap<>();
            itemTotal.put("currency_code", currency);
            itemTotal.put("value", String.format("%.2f", order.getTotalAmount() - order.getShippingFee() - order.getTaxAmount()));
            breakdown.put("item_total", itemTotal);

            Map<String, String> shipping = new HashMap<>();
            shipping.put("currency_code", currency);
            shipping.put("value", String.format("%.2f", order.getShippingFee()));
            breakdown.put("shipping", shipping);

            Map<String, String> tax = new HashMap<>();
            tax.put("currency_code", currency);
            tax.put("value", String.format("%.2f", order.getTaxAmount()));
            breakdown.put("tax_total", tax);

            amount.put("breakdown", breakdown);
            purchaseUnit.put("amount", amount);

            purchaseUnits.add(purchaseUnit);
            orderRequest.put("purchase_units", purchaseUnits);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderRequest, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/v2/checkout/orders",
                request,
                String.class
            );

            JsonNode responseJson = objectMapper.readTree(response.getBody());
            String paypalOrderId = responseJson.get("id").asText();
            String status = responseJson.get("status").asText();

            // Get approval URL
            String approvalUrl = "";
            JsonNode links = responseJson.get("links");
            if (links.isArray()) {
                for (JsonNode link : links) {
                    if ("approve".equals(link.get("rel").asText())) {
                        approvalUrl = link.get("href").asText();
                        break;
                    }
                }
            }

            // Update order with PayPal order ID
            order.setPaymentMethod("paypal");
            order.setPaypalOrderId(paypalOrderId);
            orderRepository.save(order);

            return PaymentIntentResponse.builder()
                    .approvalUrl(approvalUrl)
                    .paypalOrderId(paypalOrderId)
                    .amount(order.getTotalAmount())
                    .currency(currency)
                    .status(status)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create PayPal order: " + e.getMessage(), e);
        }
    }

    /**
     * Capture payment after PayPal approval
     */
    public boolean capturePayment(String paypalOrderId) {
        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> request = new HttpEntity<>("{}", headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/v2/checkout/orders/" + paypalOrderId + "/capture",
                request,
                String.class
            );

            JsonNode responseJson = objectMapper.readTree(response.getBody());
            String status = responseJson.get("status").asText();

            return "COMPLETED".equals(status);

        } catch (Exception e) {
            throw new RuntimeException("Failed to capture PayPal payment: " + e.getMessage(), e);
        }
    }

    /**
     * Confirm payment and update order status
     */
    @Transactional
    public Order confirmPayment(Long orderId, String paypalOrderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (capturePayment(paypalOrderId)) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            return orderRepository.save(order);
        } else {
            throw new RuntimeException("Payment capture failed");
        }
    }

    /**
     * Get payment details from PayPal
     */
    public Map<String, Object> getOrderDetails(String paypalOrderId) {
        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/v2/checkout/orders/" + paypalOrderId,
                HttpMethod.GET,
                request,
                String.class
            );

            return objectMapper.readValue(response.getBody(), Map.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to get PayPal order details: " + e.getMessage(), e);
        }
    }

    /**
     * Get payment status from PayPal
     */
    public String getPaymentStatus(String paypalOrderId) {
        Map<String, Object> orderDetails = getOrderDetails(paypalOrderId);
        return (String) orderDetails.get("status");
    }

    /**
     * Refund a captured payment
     */
    public boolean refundPayment(String captureId, Double amount, String currency) {
        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            Map<String, Object> refundRequest = new HashMap<>();
            Map<String, String> amountMap = new HashMap<>();
            amountMap.put("currency_code", currency);
            amountMap.put("value", String.format("%.2f", amount));
            refundRequest.put("amount", amountMap);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(refundRequest, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/v2/payments/captures/" + captureId + "/refund",
                request,
                String.class
            );

            JsonNode responseJson = objectMapper.readTree(response.getBody());
            String status = responseJson.get("status").asText();

            return "COMPLETED".equals(status);

        } catch (Exception e) {
            throw new RuntimeException("Failed to refund PayPal payment: " + e.getMessage(), e);
        }
    }
}
