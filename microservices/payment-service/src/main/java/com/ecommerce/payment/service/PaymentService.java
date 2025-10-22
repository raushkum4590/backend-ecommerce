package com.ecommerce.payment.service;

import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(status);
        payment.setTransactionId(transactionId);
        if (status == PaymentStatus.COMPLETED) {
            payment.setCompletedAt(LocalDateTime.now());
        }
        return paymentRepository.save(payment);
    }

    public Payment processCashOnDelivery(Long orderId) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentMethod("CASH_ON_DELIVERY");
        payment.setStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }
}

