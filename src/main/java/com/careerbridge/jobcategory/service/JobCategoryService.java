package com.careerbridge.jobcategory.service;

import com.careerbridge.jobcategory.domain.JobCategory;
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

    public List<JobCategoryResponse> getChildrenJobCategories(Long parentId){
        return jobCategoryRepository.findByParentId(parentId).stream()
                .map(JobCategoryResponse::from)
                .collect(Collectors.toUnmodifiableList());
    }

    public JobCategoryResponse getJobCategory(Long id){
        JobCategory jobCategory = jobCategoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직무 카테고리입니다."));

        return JobCategoryResponse.from(jobCategory);
    }
}
