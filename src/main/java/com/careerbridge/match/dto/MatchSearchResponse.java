package com.careerbridge.match.dto;

import com.careerbridge.match.domain.Match;
import com.careerbridge.match.domain.MatchStatus;

import java.time.LocalDateTime;

public record MatchSearchResponse(Long id,
                                  Long productId,
                                  Long menteeId,
                                  MatchStatus matchStatus,
                                  LocalDateTime requestAt,
                                  LocalDateTime approvedAt,
                                  LocalDateTime paidAt,
                                  LocalDateTime startedAt,
                                  LocalDateTime completedAt,
                                  LocalDateTime canceledAt) {
    public static MatchSearchResponse from(Match match){
        return new MatchSearchResponse(match.getId(),
                match.getProduct().getId(),
                match.getMentee().getId(),
                match.getMatchStatus(),
                match.getRequestedAt(),
                match.getApprovedAt(),
                match.getPaidAt(),
                match.getStartedAt(),
                match.getCompletedAt(),
                match.getCanceledAt()
        );
    }
}
