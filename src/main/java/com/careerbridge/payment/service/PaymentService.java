package com.careerbridge.payment.service;

import com.careerbridge.match.domain.Match;
import com.careerbridge.match.domain.MatchStatus;
import com.careerbridge.match.repository.MatchRepository;
import com.careerbridge.mentee.entity.Mentee;
import com.careerbridge.payment.entity.Payment;
import com.careerbridge.payment.entity.PaymentMethod;
import com.careerbridge.payment.entity.PaymentProvider;
import com.careerbridge.payment.entity.PaymentStatus;
import com.careerbridge.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MatchRepository matchRepository;

    @Transactional
    public Payment ready(String menteeEmail, Long matchId) {
        Match match = matchRepository.findByIdAndMenteeUserEmail(matchId, menteeEmail)
                .orElseThrow(() -> new IllegalArgumentException("결제 가능한 매칭을 찾을 수 없습니다."));

        if(match.getMatchStatus() != MatchStatus.APPROVED){
            throw new IllegalStateException("승인된 매칭만 결제할 수 있습니다");
        }

        if (paymentRepository.existsByMatchId(match.getId())) {
            throw new IllegalStateException("이미 결제가 생성된 매칭입니다");
        }

        Payment payment = Payment.ready(
                match,
                match.getProduct().getPrice(),
                PaymentProvider.TOSS,
                createOrderId()
        );

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment confirm(
            String menteeEmail,
            String orderId,
            String paymentKey,
            Integer requestedAmount
    ) {
        Payment payment = paymentRepository
                .findByOrderIdAndMatchMenteeUserEmail(orderId, menteeEmail)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다"));

        if (payment.getPaymentStatus() != PaymentStatus.READY) {
            throw new IllegalStateException("결제 대기 상태가 아닙니다");
        }

        if (!payment.getAmount().equals(requestedAmount)) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다");
        }

        LocalDateTime paidAt = LocalDateTime.now();

        payment.complete(paymentKey, PaymentMethod.CARD, paidAt);
        payment.getMatch().pay(paidAt);

        return payment;
    }

    private String createOrderId() {
        return "payment-" + UUID.randomUUID();
    }
}
