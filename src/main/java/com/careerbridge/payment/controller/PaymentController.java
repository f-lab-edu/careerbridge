package com.careerbridge.payment.controller;

import com.careerbridge.global.response.ApiResponse;
import com.careerbridge.global.security.AuthenticatedUser;
import com.careerbridge.payment.dto.PaymentConfirmRequest;
import com.careerbridge.payment.dto.PaymentReadyRequest;
import com.careerbridge.payment.dto.PaymentReadyResponse;
import com.careerbridge.payment.dto.PaymentResponse;
import com.careerbridge.payment.entity.Payment;
import com.careerbridge.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> findById(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long paymentId
    ) {
        Payment payment = paymentService.findById(authenticatedUser.email(), paymentId);

        return ResponseEntity.ok(ApiResponse.success(
                "Payment found.",
                PaymentResponse.from(payment)
        ));
    }

    @PostMapping("/ready")
    public ResponseEntity<ApiResponse<PaymentReadyResponse>> ready(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody PaymentReadyRequest request
    ) {
        Payment payment = paymentService.ready(
                authenticatedUser.email(),
                request.matchId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Payment ready.",
                        PaymentReadyResponse.from(payment)
                ));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirm(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody PaymentConfirmRequest request
    ) {
        Payment payment = paymentService.confirm(
                authenticatedUser.email(),
                request.orderId(),
                request.paymentKey(),
                request.amount()
        );

        return ResponseEntity.ok(ApiResponse.success(
                "Payment confirmed.",
                PaymentResponse.from(payment)
        ));
    }
}
