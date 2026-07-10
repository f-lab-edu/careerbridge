package com.careerbridge.payment.dto;

import com.careerbridge.payment.entity.Payment;
import com.careerbridge.payment.entity.PaymentMethod;
import com.careerbridge.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponse(Long paymentId,
                              Long matchId,
                              Integer amount,
                              PaymentStatus status,
                              PaymentMethod method,
                              String orderId,
                              String paymentKey,
                              LocalDateTime paidAt) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getMatch().getId(),
                payment.getAmount(),
                payment.getPaymentStatus(),
                payment.getPaymentMethod(),
                payment.getOrderId(),
                payment.getPaymentKey(),
                payment.getPaidAt()
        );
    }
}
