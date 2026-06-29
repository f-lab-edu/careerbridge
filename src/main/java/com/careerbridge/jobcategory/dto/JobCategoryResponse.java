package com.careerbridge.jobcategory.dto;

import com.careerbridge.jobcategory.domain.JobCategory;

import java.util.List;

public record JobCategoryResponse(
        Long jobCategoryId,
        String jobName,
        Long parentId,
        List<JobCategoryResponse> children) {

    public static JobCategoryResponse from(JobCategory jobCategory){
        return new JobCategoryResponse(jobCategory.getId(),
                jobCategory.getJobName(),
                jobCategory.getParent().getId(),
                jobCategory.getChildren().stream()
                        .map(JobCategoryResponse::from).toList());
    }

}
