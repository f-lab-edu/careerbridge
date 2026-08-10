package com.careerbridge.payment.entity;

import com.careerbridge.global.entity.BaseTimeEntity;
import com.careerbridge.match.domain.Match;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    private Match match;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider paymentProvider;

    @Column(nullable = false, unique = true)
    private String orderId;

    private String paymentKey;

    private LocalDateTime paidAt;

    private LocalDateTime failedAt;

    private LocalDateTime canceledAt;

    private Payment(
            Match match,
            Integer amount,
            PaymentStatus paymentStatus,
            PaymentMethod paymentMethod,
            PaymentProvider paymentProvider,
            String orderId,
            String paymentKey,
            LocalDateTime paidAt,
            LocalDateTime failedAt,
            LocalDateTime canceledAt
    ) {
        this.match = match;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.paymentProvider = paymentProvider;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.paidAt = paidAt;
        this.failedAt = failedAt;
        this.canceledAt = canceledAt;
    }

    public static Payment ready(
            Match match,
            Integer amount,
            PaymentProvider paymentProvider,
            String orderId
    ) {
        validateReadyPayment(match, amount, paymentProvider, orderId);

        return new Payment(
                match,
                amount,
                PaymentStatus.READY,
                null,
                paymentProvider,
                orderId,
                null,
                null,
                null,
                null
        );
    }

    public void complete(
            String paymentKey,
            PaymentMethod paymentMethod,
            LocalDateTime paidAt
    ) {
        validateCompletable(paymentKey, paymentMethod, paidAt);

        this.paymentKey = paymentKey;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = PaymentStatus.PAID;
        this.paidAt = paidAt;
    }

    public void complete(PaymentApproval approval) {
        if (approval == null) {
            throw new IllegalArgumentException("Payment approval is required.");
        }

        complete(
                approval.paymentKey(),
                approval.paymentMethod(),
                approval.approvedAt()
        );
    }

    private static void validateReadyPayment(
            Match match,
            Integer amount,
            PaymentProvider paymentProvider,
            String orderId
    ) {
        if (match == null) {
            throw new IllegalArgumentException("Match is required.");
        }

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }

        if (paymentProvider == null) {
            throw new IllegalArgumentException("Payment provider is required.");
        }

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order id is required.");
        }
    }

    private void validateCompletable(
            String paymentKey,
            PaymentMethod paymentMethod,
            LocalDateTime paidAt
    ) {
        if (paymentStatus != PaymentStatus.READY) {
            throw new IllegalStateException("Only READY payments can be completed.");
        }

        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("Payment key is required.");
        }

        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method is required.");
        }

        if (paidAt == null) {
            throw new IllegalArgumentException("Paid time is required.");
        }
    }
}
