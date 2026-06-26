package com.careerbridge.mentor.dto;

import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.mentor.entity.Mentor;

public record MentorSearchResponse(
        Long mentorId,
        String mentorName,
        String companyName,
        String position,
        Long jobCategoryId,
        int personalHistory,
        String introduction
) {

    public static MentorSearchResponse from(Mentor mentor) {
        return new MentorSearchResponse(mentor.getId(),
                mentor.getMentorName(),
                mentor.getCompanyName(),
                mentor.getPosition(),
                mentor.getJobCategory().getId(),
                mentor.getPersonalHistory(),
                mentor.getIntroduction());
    }
}
