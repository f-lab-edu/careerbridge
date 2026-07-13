package com.careerbridge.match.repository;

import com.careerbridge.match.domain.Match;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository {

    List<Match> findByProductIdAndMenteeId(Long productId, Long menteeId);
    List<Match> findByProductIdAndProductMentorId(Long productId, Long mentorId);
}
