package com.careerbridge.jobcategory.service;

import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.jobcategory.dto.JobCategoryResponse;
import com.careerbridge.jobcategory.repository.FakeJobCategoryRepository;
import com.careerbridge.jobcategory.repository.JobCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JobCategoryServiceTest {

    private JobCategoryService jobCategoryService;

    @BeforeEach
    void setUp() {
        JobCategoryRepository repository =
                new FakeJobCategoryRepository();

        jobCategoryService = new JobCategoryService(repository);
    }

    @Test
    @DisplayName("최상위 카테고리 목록을 조회할 수 있다")
    void getRootJobCategories() {
        List<JobCategoryResponse> result =
                jobCategoryService.getJobCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).jobCategoryId()).isEqualTo(1L);
        assertThat(result.get(0).jobName()).isEqualTo("개발");
        assertThat(result.get(0).children()).isEqualTo(3);
    }

    @Test
    @DisplayName("응답에 하위 카테고리까지 포함된다")
    void getJobCategoriesWithChildren() {
        List<JobCategoryResponse> result =
                jobCategoryService.getJobCategories();

        JobCategoryResponse development = result.get(0);

        assertThat(development.children()).hasSize(3);
        assertThat(development.children())
                .extracting(JobCategoryResponse::jobName)
                .contains(
                        "백엔드",
                        "프론트엔드",
                        "DevOps"
                );
    }

    @Test
    @DisplayName("하위 카테고리가 없으면 빈 목록을 반환한다")
    void leafCategoryHasEmptyChildren() {
        List<JobCategoryResponse> result =
                jobCategoryService.getJobCategories();

        assertThat(result.get(0).children())
                .allSatisfy(child ->
                        assertThat(child.children()).isEmpty()
                );
    }
}