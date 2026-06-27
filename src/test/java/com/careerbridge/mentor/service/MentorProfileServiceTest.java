package com.careerbridge.mentor.service;

import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.jobcategory.repository.FakeJobCategoryRepository;
import com.careerbridge.jobcategory.repository.JobCategoryRepository;
import com.careerbridge.mentor.dto.MentorProfileRequest;
import com.careerbridge.mentor.dto.MentorProfileResponse;
import com.careerbridge.mentor.repository.FakeMentorRepository;
import com.careerbridge.mentor.repository.MentorRepository;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import com.careerbridge.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MentorProfileServiceTest {

    private MentorService mentorService;

    @BeforeEach
    void setUp() {
        MentorRepository repository1 = new FakeMentorRepository();
        JobCategoryRepository repository2 = new FakeJobCategoryRepository();

        mentorService = new MentorService(repository1, null, repository2);
    }

    @Test
    @DisplayName("멘토_프로필을 등록할 수 있다")
    void createMentorProfile(){
        // given
        User user = User.create(
                "new-mentor@example.com",
                "EncodedPassword1!",
                "New Mentor",
                UserRole.MENTOR
        );
        MentorProfileRequest request = new MentorProfileRequest(
                "Naver",
                "백엔드 개발자",
                1L,
                10,
                "Spring 기반 백엔드 개발 경험이 있습니다."
        );

        MentorProfileResponse response = mentorService.create(user, request);

        assertThat(response.id()).isNotNull();
        assertThat(response.companyName()).isEqualTo("Naver");
        assertThat(response.position()).isEqualTo("백엔드 개발자");
        assertThat(response.jobCategoryId()).isEqualTo(1L);
        assertThat(response.personalHistory()).isEqualTo(10);
        assertThat(response.introduction()).isEqualTo("Spring 기반 백엔드 개발 경험이 있습니다.");

    }

    @Test
    @DisplayName("멘토가 아닌 사용자는 멘토 프로필을 등록할 수 없다")
    void createRejectsNonMentorUser() {
        //given
        User user = User.create(               "mentee@example.com",
                "EncodedPassword1!",
                "Mentee User",
                UserRole.MENTEE
        );

        MentorProfileRequest request = new MentorProfileRequest(
                "Naver",
                "Backend Developer",
                1L,
                10,
                "Spring 기반 백엔드 개발 경험이 있습니다."
        );


        assertThatThrownBy(() -> mentorService.create(user, request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
