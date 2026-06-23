package com.careerbridge.jobcategory.repository;

import com.careerbridge.jobcategory.domain.JobCategory;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobCategoryRepository {
    List<JobCategory> findAll();
    List<JobCategory> findByParentIsNull();
    List<JobCategory> findByParentId(Long parentId);
    Optional<JobCategory> findById(Long id);
}
