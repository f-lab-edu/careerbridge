package com.careerbridge.payment.service;

import com.careerbridge.match.domain.Match;
import com.careerbridge.match.domain.MatchStatus;
import com.careerbridge.match.repository.MatchRepository;
import com.careerbridge.payment.client.TossPaymentClient;
import com.careerbridge.payment.entity.Payment;
import com.careerbridge.payment.entity.PaymentApproval;
import com.careerbridge.payment.entity.PaymentProvider;
import com.careerbridge.payment.entity.PaymentStatus;
import com.careerbridge.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MatchRepository matchRepository;
    private final TossPaymentClient tossPaymentClient;

    public Payment findById(String menteeEmail, Long paymentId) {
        return paymentRepository.findByIdAndMatchMenteeUserEmail(paymentId, menteeEmail)
                .orElseThrow(() -> new IllegalArgumentException("寃곗젣 ?뺣낫瑜?李얠쓣 ???놁뒿?덈떎"));
    }

    @Transactional
    public Payment ready(String menteeEmail, Long matchId) {
        Match match = matchRepository.findByIdAndMenteeUserEmail(matchId, menteeEmail)
                .orElseThrow(() -> new IllegalArgumentException("결제 가능한 매칭을 찾을 수 없습니다."));

        if(match.getMatchStatus() != MatchStatus.APPROVED){
            throw new IllegalStateException("승인된 매칭만 결제할 수 있습니다");
        }

        if (paymentRepository.existsByMatchId(match.getId())){
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

        if (payment.getPaymentStatus() == PaymentStatus.PAID
                && Objects.equals(payment.getPaymentKey(), paymentKey)
                && Objects.equals(payment.getAmount(), requestedAmount)) {
            return payment;
        }

        if (payment.getPaymentStatus() != PaymentStatus.READY) {
            throw new IllegalStateException("결제 대기 상태가 아닙니다");
        }

        if (!payment.getAmount().equals(requestedAmount)) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다");
        }

        if (payment.getMatch().getMatchStatus() != MatchStatus.APPROVED) {
            throw new IllegalStateException("Only approved matches can be paid.");
        }

        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("Payment key is required.");
        }

        PaymentApproval approval = tossPaymentClient.confirm(
                paymentKey,
                orderId,
                requestedAmount
        );

        payment.complete(approval);
        payment.getMatch().pay(approval.approvedAt());

        return payment;
    }

    private String createOrderId() {
        return "payment-" + UUID.randomUUID();
    }
}
