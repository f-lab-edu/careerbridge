package com.careerbridge.jobcategory.dto;

import com.careerbridge.jobcategory.domain.JobCategory;

import java.util.List;

public record JobCategoryResponse(
        Long jobCategoryId,
        String jobName,
        JobCategory parent,
        List<JobCategory> children) {

    public static JobCategoryResponse from(JobCategory jobCategory){
        return new JobCategoryResponse(jobCategory.getId(),
                jobCategory.getJobName(),
                jobCategory.getParent(),
                jobCategory.getChildren());
    }

}
