package com.careerbridge.mentee.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MenteeProfileRequest(
        @NotNull
        Long userId,

        @NotEmpty
        List<Long> jobCategoryIdList
) {
}
