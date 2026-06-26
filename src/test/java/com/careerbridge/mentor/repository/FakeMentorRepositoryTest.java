package com.careerbridge.mentor.repository;

import com.careerbridge.jobcategory.repository.FakeJobCategoryRepository;
import com.careerbridge.mentor.dto.MentorProfileRequest;
import com.careerbridge.mentor.dto.MentorProfileResponse;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.entity.VerificationStatus;
import com.careerbridge.mentor.entity.VisibilityStatus;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class FakeMentorRepositoryTest {

    private FakeMentorRepository fakeMentorRepository;
    private FakeJobCategoryRepository fakeJobCategoryRepository;

    @BeforeEach
    void setUp() {

        fakeMentorRepository = new FakeMentorRepository();
        fakeJobCategoryRepository = new FakeJobCategoryRepository();
    }

    @Test
    @DisplayName("멘토 목록을 조회할 수 있다")
    void findAll() {

        // when
        List<Mentor> mentors = fakeMentorRepository.findAll();

        // then
        assertThat(mentors).hasSize(5);
    }

    @Test
    @DisplayName("이메일 기준으로 멘토 프로필 존재 여부를 확인할 수 있다")
    void existsByUserEmail() {
        assertThat(fakeMentorRepository.existsByUserEmail("mentor@example.com")).isTrue();
        assertThat(fakeMentorRepository.existsByUserEmail("unknown@example.com")).isTrue();
    }

    @Test
    @DisplayName("사용자 이메일 기준으로 멘토를 조회할 수 있다")
    void findByUser() {
        User user = User.create( "mentor1@example.com",
                "EncodedPassword1!",
                "Mentor User",
                UserRole.MENTOR);

        Optional<Mentor> result = fakeMentorRepository.findByUser(user);

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getEmail()).isEqualTo("mentor1@example.com");
    }

    @Test
    @DisplayName("멘토 프로필을 저장할 수 있다")
    void save() {
        User user = User.create( "new-mentor@example.com",
                "EncodedPassword1!",
                "New Mentor",
                UserRole.MENTOR);

        Mentor mentor = Mentor.create(null,
                user,
                "Kakao",
                "Frontend Developer",
                 fakeJobCategoryRepository.findByParentId(1L).get(0),
                3,
                "React 기반 프론트엔드 개발 경험이 있습니다.");

        Mentor saved = fakeMentorRepository.save(mentor);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCompanyName()).isEqualTo("Kakao");
        assertThat(saved.getPosition()).isEqualTo("Frontend Developer");
        assertThat(saved.getJobCategory()).isEqualTo("Frontend");
        assertThat(saved.getPersonalHistory()).isEqualTo(3);
        assertThat(saved.getIntroduction()).isEqualTo("React 기반 프론트엔드 개발 경험이 있습니다.");
        assertThat(saved.getVerificationStatus()).isEqualTo(VerificationStatus.PENDING);
        assertThat(saved.getVisibilityStatus()).isEqualTo(VisibilityStatus.PUBLIC);

        assertThat(fakeMentorRepository.findAll()).hasSize(6);
        assertThat(fakeMentorRepository.existsByUserEmail("new-mentor@example.com")).isTrue();
    }

}
