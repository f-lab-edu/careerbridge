package com.careerbridge.mentee.service;

import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.jobcategory.repository.JobCategoryRepository;
import com.careerbridge.mentee.entity.Mentee;
import com.careerbridge.mentee.entity.MenteeJobCategory;
import com.careerbridge.mentee.repository.MenteeRepository;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import com.careerbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenteeService {

    private final MenteeRepository menteeRepository;

    private final UserRepository userRepository;

    private final JobCategoryRepository jobCategoryRepository;


    public Mentee create(User user, List<Long> jobCategoryIdList) {
        if(user.getRole() != UserRole.MENTEE){
            throw new IllegalArgumentException("멘티만 프로필을 등록할 수 있습니다");
        }

        if(menteeRepository.existsByUserEmail(user.getEmail())){
            throw new IllegalArgumentException("이미 멘티 프로필이 존재합니다.");
        }

        List<JobCategory> jobCategories = getJobCategories(jobCategoryIdList);

        Mentee saved = Mentee.create(null, user, jobCategories);

        return menteeRepository.save(saved);
    }

    @Transactional
    public void update(User user, List<Long> jobCategoryIdList) {
        if (user.getRole() != UserRole.MENTEE) {
            throw new IllegalArgumentException("멘티만 프로필을 수정할 수 있습니다");
        }

        Mentee updated = menteeRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("멘티 프로필이 존재하지 않습니다"));

        List<JobCategory> jobCategories = getJobCategories(jobCategoryIdList);

        updated.update(jobCategories);
    }

    private List<JobCategory> getJobCategories(List<Long> jobCategoryIdList) {
        return jobCategoryIdList.stream()
                .map(id -> jobCategoryRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직무 카테고리입니다.")))
                .toList();
    }
}
