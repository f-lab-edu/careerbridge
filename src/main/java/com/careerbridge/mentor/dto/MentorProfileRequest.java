package com.careerbridge.mentor.dto;

import com.careerbridge.jobcategory.domain.JobCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MentorProfileRequest(
        @NotBlank
        String companyName,

        @NotBlank
        String position,

        @NotBlank
        Long jobCategoryId,

        @NotNull
        @Min(0)
        Integer personalHistory,

        @NotBlank
        String introduction) {

}
