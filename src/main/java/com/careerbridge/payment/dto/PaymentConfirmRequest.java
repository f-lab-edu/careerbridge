package com.careerbridge.payment.dto;

public record PaymentConfirmRequest(String orderId,
                                    String paymentKey,
                                    Integer amount) {
}
