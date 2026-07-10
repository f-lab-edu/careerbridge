package com.careerbridge.payment.dto;

import com.careerbridge.payment.entity.PaymentStatus;

public record PaymentReadyResponse(Long paymentId,
                                   String orderId,
                                   Integer amount,
                                   String orderName,
                                   PaymentStatus status) {
}
