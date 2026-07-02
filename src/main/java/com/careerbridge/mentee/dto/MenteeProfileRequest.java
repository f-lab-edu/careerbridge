package com.careerbridge.mentee.dto;

import com.careerbridge.jobcategory.domain.JobCategory;

import java.util.List;

public record MenteeProfileRequest(
        List<Long> jobCategoryIdList

) {
}
