package com.careerbridge.payment.repository;

import com.careerbridge.payment.entity.Payment;

import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    Optional<Payment> findByOrderIdAndMatchMenteeUserEmail(String orderId, String email);

    Optional<Payment> findByMatchId(Long matchId);

    boolean existsByMatchId(Long matchId);
}
