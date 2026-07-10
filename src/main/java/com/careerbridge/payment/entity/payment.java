package com.careerbridge.payment.entity;
// git test용 주석 추가
import com.careerbridge.global.entity.BaseTimeEntity;
import com.careerbridge.match.domain.Match;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class payment extends BaseTimeEntity {
    private Long id;

    @OneToOne
    private Match match;

    private BigDecimal amount;

    private PaymentStatus paymentStatus;

    private PaymentMethod paymentMethod;

    private String transactionId;

    private LocalDateTime paidAt;

    public payment(Long id, Match match, BigDecimal amount,
                   PaymentStatus paymentStatus, PaymentMethod paymentMethod,
                   String transactionId, LocalDateTime paidAt){
        this.id = id;
        this.match = match;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.paidAt = paidAt;
    }
}
