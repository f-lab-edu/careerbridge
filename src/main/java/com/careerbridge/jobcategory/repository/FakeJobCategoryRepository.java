package com.careerbridge.jobcategory.repository;

import com.careerbridge.jobcategory.domain.JobCategory;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FakeJobCategoryRepository implements JobCategoryRepository{
    private final Map<Long, JobCategory> categoryDictionary;

    public FakeJobCategoryRepository() {


        JobCategory development = new JobCategory(
                1L,
                "개발",
                null,
                new ArrayList<>()
        );



        JobCategory backend = new JobCategory(
                2L,
                "백엔드",
                development,
                List.of()
        );

        JobCategory frontend = new JobCategory(
                3L,
                "프론트엔드",
                development,
                List.of()
        );

        JobCategory devOps = new JobCategory(
                4L,
                "DevOps",
                development,
                List.of()
        );

        development.getChildren().addAll(
                List.of(backend, frontend, devOps)
        );



        this.categoryDictionary = Map.of(
                1L, development,
                2L, backend,
                3L, frontend,
                4L, devOps
        );
    }
    @Override
    public List<JobCategory> findAll() {
        return categoryDictionary.values().stream()
                .toList();
    }

    @Override
    public List<JobCategory> findByParentIsNull() {
        return categoryDictionary.values().stream()
                .filter(category -> category.getParent() == null).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<JobCategory> findByParentId(Long parentId) {
        return List.of();
    }

    @Override
    public Optional<JobCategory> findById(Long id) {
        return Optional.empty();
    }
}

