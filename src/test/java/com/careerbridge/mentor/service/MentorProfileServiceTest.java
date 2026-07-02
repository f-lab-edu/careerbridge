package com.careerbridge.mentor.service;

import com.careerbridge.jobcategory.repository.FakeJobCategoryRepository;
import com.careerbridge.mentor.dto.MentorProfileRequest;
import com.careerbridge.mentor.dto.MentorProfileResponse;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.repository.FakeMentorRepository;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MentorProfileServiceTest {

    private MentorService mentorService;
    private FakeJobCategoryRepository fakeJobCategoryRepository;

    @BeforeEach
    void setUp() {
        fakeJobCategoryRepository = new FakeJobCategoryRepository();
        mentorService = new MentorService(
                new FakeMentorRepository(),
                null,
                fakeJobCategoryRepository);
    }

    @Test
    @DisplayName("멘토_프로필을 등록할 수 있다")
    void createMentorProfile(){
        User user = User.create(
                "new-mentor@example.com",
                "EncodedPassword1!",
                "New Mentor",
                UserRole.MENTOR
        );
        MentorProfileRequest request = new MentorProfileRequest(
                null,
                "Naver",
                "백엔드 개발자",
                2L,
                10,
                "Spring 기반 백엔드 개발 경험이 있습니다."
        );
        Mentor mentor = Mentor.create(
                null,
                user,
                request.companyName(),
                request.position(),
                fakeJobCategoryRepository.findById(request.jobCategoryId()).orElseThrow(),
                request.personalHistory(),
                request.introduction()
        );

        MentorProfileResponse response = MentorProfileResponse.from(mentorService.create(mentor));

        assertThat(response.id()).isNotNull();
        assertThat(response.companyName()).isEqualTo("Naver");
        assertThat(response.position()).isEqualTo("백엔드 개발자");
        assertThat(response.jobCategoryId()).isEqualTo(2L);
        assertThat(response.personalHistory()).isEqualTo(10);
        assertThat(response.introduction()).isEqualTo("Spring 기반 백엔드 개발 경험이 있습니다.");
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

        MentorProfileRequest request = new MentorProfileRequest(
                null,
                "Naver",
                "Backend Developer",
                2L,
                10,
                "Spring 기반 백엔드 개발 경험이 있습니다."
        );
        Mentor mentor = Mentor.create(
                null,
                user,
                request.companyName(),
                request.position(),
                fakeJobCategoryRepository.findById(request.jobCategoryId()).orElseThrow(),
                request.personalHistory(),
                request.introduction()
        );

        assertThatThrownBy(() -> mentorService.create(mentor))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
