package com.careerbridge.mentor.dto;

import com.careerbridge.mentor.entity.Mentor;

public record MentorProfileResponse(Long id,
                                    String companyName,
                                    String position,
                                    Long jobCategoryId,
                                    Integer personalHistory,
                                    String introduction) {
    public static MentorProfileResponse from(Mentor mentor) {
        return new MentorProfileResponse(mentor.getId(),
                mentor.getCompanyName(),
                mentor.getPosition(),
                mentor.getJobCategory().getId(),
                mentor.getPersonalHistory(),
                mentor.getIntroduction());
    }
}
