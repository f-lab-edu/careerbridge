package com.careerbridge.jobcategory.repository;

import com.careerbridge.jobcategory.domain.JobCategory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FakeJobCategoryRepository implements JobCategoryRepository{
    private final List<JobCategory> categories;

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


        this.categories = List.of(
                development,
                backend,
                frontend,
                devOps
        );
    }
    @Override
    public List<JobCategory> findAll() {
        return categories;
    }

    @Override
    public List<JobCategory> findByParentIsNull() {
        return categories.stream()
                .filter(category -> category.getParent() == null).collect(Collectors.toUnmodifiableList());
    }
}

