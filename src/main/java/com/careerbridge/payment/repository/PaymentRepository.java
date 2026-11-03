package com.careerbridge.payment.repository;

import com.careerbridge.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdAndMatchMenteeUserEmail(Long id, String email);

    Optional<Payment> findByOrderIdAndMatchMenteeUserEmail(String orderId, String email);

    Optional<Payment> findByMatchId(Long matchId);

    boolean existsByMatchId(Long matchId);
}
