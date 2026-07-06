package com.careerbridge.match.domain;

import com.careerbridge.global.entity.BaseTimeEntity;
import com.careerbridge.mentee.entity.Mentee;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Match extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToOne
    @JoinColumn(name = "mentee_id", nullable = false)
    private Mentee mentee;

    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime paidAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private LocalDateTime canceledAt;

    public Match(Long id,
                 Product product,
                 Mentee mentee,
                 MatchStatus matchStatus,
                 LocalDateTime requestedAt,
                 LocalDateTime approvedAt,
                 LocalDateTime paidAt,
                 LocalDateTime startedAt,
                 LocalDateTime completedAt,
                 LocalDateTime canceledAt){
        this.id = id;
        this.product = product;
        this.mentee = mentee;
        this.matchStatus = matchStatus;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.paidAt = paidAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.canceledAt = canceledAt;
    }

    public static Match create(Long id,
                               Product product,
                               Mentee mentee,
                               MatchStatus matchStatus,
                               LocalDateTime requestedAt,
                               LocalDateTime approvedAt,
                               LocalDateTime paidAt,
                               LocalDateTime startedAt,
                               LocalDateTime completedAt,
                               LocalDateTime canceledAt
                               ){
        return new Match(id,
                product,
                mentee,
                matchStatus,
                requestedAt,
                approvedAt,
                paidAt,
                startedAt,
                completedAt,
                canceledAt);
    }

    public void request(LocalDateTime requestAt){
        this.matchStatus = MatchStatus.REQUESTED;
        this.requestedAt = requestAt;
    }

    public void approve(LocalDateTime approvedAt) {
        this.matchStatus = MatchStatus.APPROVED;
        this.approvedAt = approvedAt;
    }

    public void pay(LocalDateTime paidAt) {
        if(this.matchStatus != MatchStatus.APPROVED){
            throw new IllegalStateException("승인된 매칭만 결제할 수 있습니다");
        }

        this.matchStatus = MatchStatus.PAID;
        this.paidAt = paidAt;
    }

    public void start(LocalDateTime startedAt){
        if(this.matchStatus != MatchStatus.PAID){
            throw new IllegalStateException("결제가 된 매칭만 결제할 수 있습니다");
        }

        this.matchStatus = MatchStatus.IN_PROGRESS;
        this.startedAt = startedAt;
    }

    public void complete(LocalDateTime completedAt) {
        this.matchStatus = MatchStatus.COMPLETED;
        this.completedAt = completedAt;
    }

    public void cancel(LocalDateTime canceledAt) {
        this.matchStatus = MatchStatus.CANCELED;
        this.canceledAt = canceledAt;
    }
}
