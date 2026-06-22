package com.careerbridge.jobcategory.repository;

import com.careerbridge.jobcategory.domain.JobCategory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FakeJobCategoryRepositoryTest {

    private FakeJobCategoryRepository repository;

    @BeforeEach
    void setUp(){
        repository = new FakeJobCategoryRepository();
    }


    @Test
    @DisplayName("전체 직무 카테고리를 조회할 수 있다")
    void findAll() {
        List<JobCategory> result = repository.findAll();

        assertThat(result).hasSize(4);
        assertThat(result)
                .extracting(JobCategory::getJobName)
                .containsExactly(
                        "개발",
                        "백엔드",
                        "프론트엔드",
                        "DevOps"
                );
    }

    @Test
    @DisplayName("최상위 직무 카테고리만 조회할 수 있다")
    void findByParentIsNull() {

        List<JobCategory> result = repository.findByParentIsNull();

        assertThat(result).hasSize(1);

        JobCategory development = result.get(0);

        assertThat(development.getJobName()).isEqualTo("개발");
        assertThat(development.getParent()).isNull();
    }

    @Test
    @DisplayName("상위 카테고리에 하위 카테고리가 포함된다")
    void rootCategoryContainsChildren() {
        JobCategory development =
                repository.findByParentIsNull().get(0);

        assertThat(development.getChildren()).hasSize(3);
        assertThat(development.getChildren())
                .extracting(JobCategory::getJobName)
                .containsExactly(
                        "백엔드",
                        "프론트엔드",
                        "DevOps"
                );
    }

    @Test
    @DisplayName("하위 카테고리는 상위 카테고리를 참조한다")
    void childCategoryReferencesParent() {
        JobCategory development =
                repository.findByParentIsNull().get(0);

        assertThat(development.getChildren())
                .allSatisfy(child ->
                        assertThat(child.getParent())
                                .isSameAs(development)
                );
    }
}
