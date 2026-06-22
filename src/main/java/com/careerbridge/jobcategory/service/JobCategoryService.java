package com.careerbridge.jobcategory.service;

import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.jobcategory.dto.JobCategoryRequest;
import com.careerbridge.jobcategory.dto.JobCategoryResponse;
import com.careerbridge.jobcategory.repository.JobCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobCategoryService {
     private final JobCategoryRepository jobCategoryRepository;

    public List<JobCategoryResponse> getJobCategories(){
        return jobCategoryRepository.findByParentIsNull().stream()
                .map(JobCategoryResponse::from)
                .collect(Collectors.toUnmodifiableList());
    }
}
