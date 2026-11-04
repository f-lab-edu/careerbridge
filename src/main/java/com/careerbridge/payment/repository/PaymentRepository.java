package com.careerbridge.payment.repository;

import com.careerbridge.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdAndMatchMenteeUserEmail(Long id, String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Payment> findByOrderIdAndMatchMenteeUserEmail(String orderId, String email);

    Optional<Payment> findByMatchId(Long matchId);

    boolean existsByMatchId(Long matchId);

}
