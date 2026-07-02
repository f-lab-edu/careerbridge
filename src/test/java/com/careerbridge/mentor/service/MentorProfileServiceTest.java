package com.careerbridge.mentor.service;

import com.careerbridge.jobcategory.repository.FakeJobCategoryRepository;
import com.careerbridge.mentor.dto.MentorProfileRequest;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.repository.FakeMentorRepository;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import com.careerbridge.user.entity.UserStatus;
import com.careerbridge.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MentorProfileServiceTest {

    private UserRepository userRepository;
    private MentorService mentorService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);

        mentorService = new MentorService(
                new FakeMentorRepository(),
                userRepository,
                new FakeJobCategoryRepository()
        );
    }

    @Test
    @DisplayName("멘토 프로필을 등록할 수 있다")
    void createMentorProfile() {
        User user = User.create(
                "new-mentor@example.com",
                "EncodedPassword1!",
                "New Mentor",
                UserRole.MENTOR
        );

        when(userRepository.findByEmailAndStatus(user.getEmail(), UserStatus.ACTIVE))
                .thenReturn(Optional.of(user));

        MentorProfileRequest request = new MentorProfileRequest(
                null,
                "Naver",
                "Backend Developer",
                2L,
                10,
                "Spring 기반 백엔드 개발 경험이 있습니다."
        );

        Mentor saved = mentorService.create(
                user.getEmail(),
                request.companyName(),
                request.position(),
                request.jobCategoryId(),
                request.personalHistory(),
                request.introduction()
        );

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser().getEmail()).isEqualTo("new-mentor@example.com");
        assertThat(saved.getCompanyName()).isEqualTo("Naver");
        assertThat(saved.getPosition()).isEqualTo("Backend Developer");
        assertThat(saved.getJobCategory().getId()).isEqualTo(2L);
        assertThat(saved.getPersonalHistory()).isEqualTo(10);
        assertThat(saved.getIntroduction()).isEqualTo("Spring 기반 백엔드 개발 경험이 있습니다.");
    }

    @Test
    @DisplayName("멘토가 아닌 사용자는 멘토 프로필을 등록할 수 없다")
    void createRejectsNonMentorUser() {
        User user = User.create(
                "mentee@example.com",
                "EncodedPassword1!",
                "Mentee User",
                UserRole.MENTEE
        );

        when(userRepository.findByEmailAndStatus(user.getEmail(), UserStatus.ACTIVE))
                .thenReturn(Optional.of(user));

        MentorProfileRequest request = new MentorProfileRequest(
                null,
                "Naver",
                "Backend Developer",
                2L,
                10,
                "Spring 기반 백엔드 개발 경험이 있습니다."
        );

        assertThatThrownBy(() -> mentorService.create(
                user.getEmail(),
                request.companyName(),
                request.position(),
                request.jobCategoryId(),
                request.personalHistory(),
                request.introduction()
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지 않는 직무 카테고리이면 멘토 프로필을 등록할 수 없다")
    void createRejectsUnknownJobCategory() {
        User user = User.create(
                "new-mentor@example.com",
                "EncodedPassword1!",
                "New Mentor",
                UserRole.MENTOR
        );

        when(userRepository.findByEmailAndStatus(user.getEmail(), UserStatus.ACTIVE))
                .thenReturn(Optional.of(user));

        MentorProfileRequest request = new MentorProfileRequest(
                null,
                "Naver",
                "Backend Developer",
                999L,
                10,
                "Spring 기반 백엔드 개발 경험이 있습니다."
        );

        assertThatThrownBy(() -> mentorService.create(
                user.getEmail(),
                request.companyName(),
                request.position(),
                request.jobCategoryId(),
                request.personalHistory(),
                request.introduction()
        )).isInstanceOf(IllegalArgumentException.class);
    }
}