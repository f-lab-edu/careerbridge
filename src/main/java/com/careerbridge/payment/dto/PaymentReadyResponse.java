package com.careerbridge.payment.dto;

import com.careerbridge.payment.entity.Payment;
import com.careerbridge.payment.entity.PaymentStatus;

public record PaymentReadyResponse(
        Long paymentId,
        String orderId,
        Integer amount,
        String orderName,
        PaymentStatus status
) {

    public static PaymentReadyResponse from(Payment payment) {
        return new PaymentReadyResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getMatch().getProduct().getTitle(),
                payment.getPaymentStatus()
        );
    }
}
