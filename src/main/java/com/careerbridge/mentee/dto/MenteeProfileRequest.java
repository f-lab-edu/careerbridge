package com.careerbridge.mentee.dto;

import com.careerbridge.jobcategory.domain.JobCategory;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record MenteeProfileRequest(
        @NotBlank
        Long userId,
        List<Long> jobCategoryIdList
) {
}
