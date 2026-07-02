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

    public List<Mentor> searchMentors() {
        return mentorRepository.findAll();
    }

    public Mentor create(Mentor mentor) {
        if (mentor.getUser().getRole() != UserRole.MENTOR) {
            throw new IllegalArgumentException("멘토만 프로필을 등록할 수 있습니다");
        }
        if (mentorRepository.existsByUserEmail(mentor.getUser().getEmail())) {
            throw new IllegalArgumentException("이미 멘토 프로필이 존재합니다");
        }

        JobCategory jobCategory = jobCategoryRepository.findById(mentor.getJobCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직무 카테고리입니다."));

        mentor = Mentor.create(null,
                mentor.getUser(),
                mentor.getCompanyName(),
                mentor.getPosition(),
                jobCategory,
                mentor.getPersonalHistory(),
                mentor.getIntroduction());

        Mentor saved = mentorRepository.save(mentor);
        return saved;
    }

    @Transactional
    public void update(Mentor mentor) {

        if (mentor.getUser().getRole() != UserRole.MENTOR) {
            throw new IllegalArgumentException("멘토 프로필만 수정할 수 있습니다.");
        }

        User user = userRepository.findByEmailAndStatus(mentor.getUser().getEmail(), UserStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));

        mentor = mentorRepository.findByUser(mentor.getUser())
                .orElseThrow(() -> new IllegalArgumentException("본인 프로필만 수정할 수 있습니다."));

        JobCategory jobCategory = jobCategoryRepository.findById(mentor.getJobCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직무 카테고리입니다."));

        mentor.update(
                mentor.getCompanyName(),
                mentor.getPosition(),
                jobCategory,
                mentor.getPersonalHistory(),
                mentor.getIntroduction()
        );
    }
}
