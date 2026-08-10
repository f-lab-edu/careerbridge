package com.careerbridge.payment.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentReadyRequest(
        @NotNull Long matchId
) {
}
