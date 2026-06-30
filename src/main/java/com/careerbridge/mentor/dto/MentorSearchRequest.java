package com.careerbridge.mentor.dto;

import com.careerbridge.jobcategory.domain.JobCategory;

public record MentorSearchRequest(
        Long jobCategoryId,
        String keyword) {
}
