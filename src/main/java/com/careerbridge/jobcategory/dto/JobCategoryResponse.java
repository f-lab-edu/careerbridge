package com.careerbridge.jobcategory.dto;

import com.careerbridge.jobcategory.domain.JobCategory;

import java.util.List;

public record JobCategoryResponse(
        Long jobCategoryId,
        String jobName,
        List<JobCategoryResponse> children) {

    public static JobCategoryResponse from(JobCategory jobCategory){
        return new JobCategoryResponse(jobCategory.getId(),
                jobCategory.getJobName(),
                jobCategory.getChildren().stream()
                        .map(JobCategoryResponse::from).toList());
    }

}
