package com.careerbridge.jobcategory.controller;

import com.careerbridge.jobcategory.dto.JobCategoryRequest;
import com.careerbridge.jobcategory.dto.JobCategoryResponse;
import com.careerbridge.jobcategory.service.JobCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobCategoryController {

    private final JobCategoryService jobCategoryService;


    @GetMapping("api/job-categories")
    public List<JobCategoryResponse> getChildrenJobCategories(
            @RequestParam(required = false) Long id
    ){
        if(id != null){
            return jobCategoryService.getChildrenJobCategories(id);
        }
        return jobCategoryService.getJobCategories();
    }

    @GetMapping("/api/job-categories/{id}")
    public JobCategoryResponse getJobCategory(@PathVariable Long id){
        return jobCategoryService.getJobCategory(id);
    }

}
