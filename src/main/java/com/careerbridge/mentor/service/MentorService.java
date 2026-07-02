package com.careerbridge.mentor.service;

import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.jobcategory.repository.JobCategoryRepository;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.repository.MentorRepository;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import com.careerbridge.user.entity.UserStatus;
import com.careerbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorService {

    private final MentorRepository mentorRepository;

    private final UserRepository userRepository;

    private final JobCategoryRepository jobCategoryRepository;

    public List<Mentor> searchMentors(Long jobCategoryId, String keyword) {
        return mentorRepository.search(jobCategoryId,keyword);
    }

    public Mentor create(
            String email,
            String companyName,
            String position,
            Long jobCategoryId,
            Integer personalHistory,
            String introduction
    ) {
        User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (user.getRole() != UserRole.MENTOR) {
            throw new IllegalArgumentException("멘토만 프로필을 등록할 수 있습니다.");
        }

        if (mentorRepository.existsByUserEmail(user.getEmail())) {
            throw new IllegalArgumentException("이미 멘토 프로필이 존재합니다.");
        }

        JobCategory jobCategory = jobCategoryRepository.findById(jobCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직무 카테고리입니다."));

        Mentor mentor = Mentor.create(
                null,
                user,
                companyName,
                position,
                jobCategory,
                personalHistory,
                introduction
        );

        return mentorRepository.save(mentor);
    }

    @Transactional
    public void update(
            String email,
            String companyName,
            String position,
            Long jobCategoryId,
            Integer personalHistory,
            String introduction
    ) {
        User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Mentor mentor = mentorRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("멘토 프로필이 존재하지 않습니다."));

        JobCategory jobCategory = jobCategoryRepository.findById(jobCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직무 카테고리입니다."));

        mentor.update(
                companyName,
                position,
                jobCategory,
                personalHistory,
                introduction
        );
    }
}
