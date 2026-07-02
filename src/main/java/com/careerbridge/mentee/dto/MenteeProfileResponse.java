package com.careerbridge.mentee.dto;

import com.careerbridge.mentee.entity.Mentee;
import com.careerbridge.mentee.entity.MenteeJobCategory;

import java.util.List;

public record MenteeProfileResponse(
        Long id,
        List<Long> jobCategoryList
) {
    public static MenteeProfileResponse from(Mentee mentee){
        return new MenteeProfileResponse(
                mentee.getId(),
                mentee.getJobCategories().stream()
                        .map(menteeJobCategory -> menteeJobCategory.getJobCategory().getId()).toList());
    }
}
