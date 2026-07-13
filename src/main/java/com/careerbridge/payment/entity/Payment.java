package com.careerbridge.payment.entity;

import com.careerbridge.global.entity.BaseTimeEntity;
import com.careerbridge.match.domain.Match;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Match match;

    private Integer amount;;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentProvider paymentProvider;

    private String orderId;

    private String paymentKey;

    private LocalDateTime paidAt;

    private LocalDateTime failedAt;

    private LocalDateTime canceledAt;

    public Payment(Long id, Match match, Integer amount,
                   PaymentStatus paymentStatus, PaymentMethod paymentMethod,
                   PaymentProvider paymentProvider,
                   String orderId, String paymentKey,
                   LocalDateTime paidAt, LocalDateTime failedAt,
                   LocalDateTime canceledAt){
        this.id = id;
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
        validatePayment(match, amount, paymentProvider, orderId);

        return new Payment(
                null,
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

    public void complete(String paymentKey, PaymentMethod method, LocalDateTime paidAt){
        validatePayment(match, amount, paymentProvider, orderId);

        this.paymentKey = paymentKey;
        this.paymentMethod = method;
        this.paymentStatus = PaymentStatus.PAID;
        this.paidAt = paidAt;
    }

    private static void validatePayment(
            Match match,
            Integer amount,
            PaymentProvider paymentProvider,
            String orderId
    ) {
        if (match == null) {
            throw new IllegalArgumentException("매칭 정보가 필요합니다.");
        }

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }

        if (paymentProvider == null) {
            throw new IllegalArgumentException("결제 제공자가 필요합니다.");
        }

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("주문 번호가 필요합니다.");
        }
    }
}
