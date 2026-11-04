package com.careerbridge.match.repository;

import com.careerbridge.match.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByProductIdAndMenteeId(Long productId, Long menteeId);

    List<Match> findByProductIdAndProductMentorId(Long productId, Long mentorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Match> findByIdAndMenteeUserEmail(Long matchId, String email);
}
