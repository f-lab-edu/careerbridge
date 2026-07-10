package com.careerbridge.payment.service;

import com.careerbridge.match.domain.Match;
import com.careerbridge.match.repository.MatchRepository;
import com.careerbridge.match.service.MatchService;
import com.careerbridge.mentee.entity.Mentee;
import com.careerbridge.mentee.repository.MenteeRepository;
import com.careerbridge.payment.entity.Payment;
import com.careerbridge.payment.entity.PaymentMethod;
import com.careerbridge.payment.entity.PaymentProvider;
import com.careerbridge.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final MenteeRepository menteeRepository;

    @Transactional
    public Payment ready(Payment payment) {

        Mentee mentee = getMentee(payment.getMatch().getMentee().getUser().getEmail());
        Match match = getMatch(payment.getMatch().getId());

        validateMatchOwner(match, mentee);
        validatePayableMatch(match);
        validateNotExistsPayment(match);

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
        Mentee mentee = getMentee(menteeEmail);
        Payment payment = getPaymentByOrderId(orderId);

        validatePaymentOwner(payment, mentee);
        validateReadyPayment(payment);
        validateAmount(payment, requestedAmount);

        // 나중에 PG confirm API 호출
        // TossConfirmResponse response = tossPaymentClient.confirm(...);

        payment.complete(paymentKey, PaymentMethod.CARD, LocalDateTime.now());
        payment.getMatch().pay(LocalDateTime.now());

        return payment;
    }

    private void checkVaildPayment(Payment payment){
        if(paymentRepository.existsByMatchId(payment.getMatch().getId())){
            throw new IllegalStateException("없는 결제입니다");
        }

        Payment check = paymentRepository.findByMatchId(payment.getMatch().getId())
                .orElseThrow(() ->new IllegalArgumentException("없는 결제입니다"));

        if(check != payment){
            throw new IllegalStateException("같은 결제가 아닙니다");
        }

    }
}
