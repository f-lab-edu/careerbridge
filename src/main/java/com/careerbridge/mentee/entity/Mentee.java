package com.careerbridge.mentee.entity;

import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "mentee")
@Getter
public class Mentee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "mentee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenteeJobCategory> jobCategories = new ArrayList<>();


    private void validateJobCategories(List<JobCategory> categories) {
        if (categories == null || categories.isEmpty() || categories.size() > 3) {
            throw new IllegalArgumentException("멘티 관심 직무는 1개 이상 3개 이하로 선택해야 합니다.");
        }

        long distinctCount = categories.stream()
                .map(JobCategory::getId)
                .distinct()
                .count();

        if (distinctCount != categories.size()) {
            throw new IllegalArgumentException("동일한 직무는 중복 선택할 수 없습니다.");
        }
    }

    protected Mentee() {

    }

    public Mentee(Long id, User user, List<JobCategory> categories){
        this.id = id;
        this.user = user;
        update(categories);
    }

    public String getMenteeName(){
        return user.getName();
    }

    public void update(List<JobCategory> categories) {
        validateJobCategories(categories);

        Set<Long> requestedCategoryIds = categories.stream()
                .map(JobCategory::getId)
                .collect(Collectors.toSet());

        this.jobCategories.removeIf(menteeJobCategory ->
                !requestedCategoryIds.contains(menteeJobCategory.getJobCategory().getId())
        );

        Set<Long> currentCategoryIds = jobCategories.stream()
                .map(menteeJobCategory -> menteeJobCategory.getJobCategory().getId())
                .collect(Collectors.toSet());

        for (JobCategory category : categories) {
            if (!currentCategoryIds.contains(category.getId())) {
                jobCategories.add(MenteeJobCategory.create(this, category));
            }
        }
    }

    public static Mentee create(Long id,
                                User user,
                                List<JobCategory> jobCategories){
        return new Mentee(id, user, jobCategories);
    }

}
