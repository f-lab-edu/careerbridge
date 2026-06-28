package com.careerbridge.mentee.service;

import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.jobcategory.repository.JobCategoryRepository;
import com.careerbridge.mentee.dto.MenteeProfileRequest;
import com.careerbridge.mentee.dto.MenteeProfileResponse;
import com.careerbridge.mentee.entity.Mentee;
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


    public MenteeProfileResponse create(User user, MenteeProfileRequest request) {
        if(user.getRole() != UserRole.MENTEE){
            throw new IllegalArgumentException("멘티만 프로필을 등록할 수 있습니다");
        }

        if(menteeRepository.existsByUserEmail(user.getEmail())){
            throw new IllegalArgumentException("이미 멘티 프로필이 존재합니다.");
        }

        List<JobCategory> jobCategories = getJobCategories(request.jobCategoryIdList());

        Mentee mentee = Mentee.create(null, user, jobCategories);
        Mentee saved = menteeRepository.save(mentee);

        return MenteeProfileResponse.from(saved);
    }

    @Transactional
    public MenteeProfileResponse update(User user, MenteeProfileRequest request) {
        if (user.getRole() != UserRole.MENTEE) {
            throw new IllegalArgumentException("멘티만 프로필을 수정할 수 있습니다");
        }

        Mentee mentee = menteeRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("멘티 프로필이 존재하지 않습니다"));

        List<JobCategory> jobCategories = getJobCategories(request.jobCategoryIdList());

        mentee.update(jobCategories);

        return MenteeProfileResponse.from(mentee);
    }

    private List<JobCategory> getJobCategories(List<Long> jobCategoryIds) {
        return jobCategoryIds.stream()
                .map(id -> jobCategoryRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직무 카테고리입니다.")))
                .toList();
    }
}
