package com.careerbridge.Mentor.Service;

import com.careerbridge.mentor.dto.MentorSearchRequest;
import com.careerbridge.mentor.dto.MentorSearchResponse;
import com.careerbridge.mentor.repository.FakeMentorRepository;
import com.careerbridge.mentor.repository.MentorRepository;
import com.careerbridge.mentor.service.MentorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("멘토탐색")
public class MentorSearchServiceTest {

    private MentorService mentorService;

    @BeforeEach
    void setUp(){
        MentorRepository repository =
                new FakeMentorRepository();

        mentorService = new MentorService(repository);
    }

    @Test
    @DisplayName("승인되고 공개된 멘토만 조회된다")
    void searchOnlyApprovedAndPublicMentors() {
        MentorSearchRequest request = new MentorSearchRequest(null, null);

        List<MentorSearchResponse> result =
                mentorService.searchMentors(request);

        assertThat(result)
                .extracting(MentorSearchResponse::mentorName)
                .containsExactly(
                        "김백엔드",
                        "이프론트",
                        "박데브옵스"
                );
    }

    @Test
    @DisplayName("카테고리로 멘토를 검색할 수 있다")
    void searchByCategory(){
        MentorSearchRequest request = new MentorSearchRequest("Backend", null);

        List<MentorSearchResponse> result = mentorService.searchMentors(request);


        assertThat(result).hasSize(1);

        assertThat(result.get(0).mentorName())
                .isEqualTo("김백엔드");
    }

    @Test
    @DisplayName("키워드로 검색할 수 있다")
    void searchByKeyword() {
        // given
        MentorSearchRequest request =
                new MentorSearchRequest(
                        null,
                        "Spring"
                );

        // when
        List<MentorSearchResponse> result =
                mentorService.searchMentors(request);

        // then
        assertThat(result).hasSize(1);

        assertThat(result.get(0).mentorName())
                .isEqualTo("김백엔드");
    }



    @Test
    @DisplayName("카테고리와 경력 조건을 함께 검색할 수 있다")
    void searchByCategoryAndExperience() {

        // given
        MentorSearchRequest request =
                new MentorSearchRequest(
                        "Backend",
                        null
                );

        // when
        List<MentorSearchResponse> result =
                mentorService.searchMentors(request);

        // then
        assertThat(result).hasSize(1);

        assertThat(result.get(0).mentorName())
                .isEqualTo("김백엔드");
    }

    @Test
    @DisplayName("조건에 맞는 멘토가 없으면 빈 리스트를 반환한다")
    void returnEmptyList() {

        // given
        MentorSearchRequest request =
                new MentorSearchRequest(
                        "요리사",
                        "Cook"
                );

        // when
        List<MentorSearchResponse> result =
                mentorService.searchMentors(request);

        // then
        assertThat(result).isEmpty();
    }
}
