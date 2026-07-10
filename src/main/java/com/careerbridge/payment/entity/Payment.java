package com.careerbridge.payment.entity;

import com.careerbridge.global.entity.BaseTimeEntity;
import com.careerbridge.match.domain.Match;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
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

    public void complete(String paymentKey, PaymentMethod method, LocalDateTime paidAt){
        if(this.paymentStatus != PaymentStatus.READY){
            throw new IllegalStateException("결제 대기 상태에서만 승인할 수 있습니다");
        }
        this.paymentKey = paymentKey;
        this.paymentMethod = method;
        this.paymentStatus = PaymentStatus.PAID;
        this.paidAt = paidAt;
    }
}
