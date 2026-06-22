package com.careerbridge.jobcategory.repository;

import com.careerbridge.jobcategory.domain.JobCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobCategoryRepository {
    List<JobCategory> findAll();
    List<JobCategory> findByParentIsNull();
}
