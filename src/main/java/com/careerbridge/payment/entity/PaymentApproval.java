package com.careerbridge.payment.entity;

import java.time.LocalDateTime;

public record PaymentApproval(
        String paymentKey,
        PaymentMethod paymentMethod,
        LocalDateTime approvedAt
) {

    public PaymentApproval {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("Payment key is required.");
        }

        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method is required.");
        }

        if (approvedAt == null) {
            throw new IllegalArgumentException("Approved time is required.");
        }
    }
}
