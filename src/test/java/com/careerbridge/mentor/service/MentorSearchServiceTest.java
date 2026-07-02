package com.careerbridge.mentor.service;

import com.careerbridge.mentor.dto.MentorSearchRequest;
import com.careerbridge.mentor.dto.MentorSearchResponse;
import com.careerbridge.mentor.repository.FakeMentorRepository;
import com.careerbridge.mentor.repository.MentorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("멘토탐색")
public class MentorSearchServiceTest {

    private MentorService mentorService;

    @BeforeEach
    void setUp(){
        MentorRepository repository =
                new FakeMentorRepository();

        mentorService = new MentorService(repository, null, null);
    }

    @Test
    @DisplayName("승인되고 공개된 멘토만 조회된다")
    void searchOnlyApprovedAndPublicMentors() {
        MentorSearchRequest request = new MentorSearchRequest(null, null);

        List<MentorSearchResponse> result =
                mentorService.searchMentors()
                        .stream()
                        .filter(mentor -> request.jobCategoryId() == null || mentor.matchesCategory(request.jobCategoryId()))
                        .filter(mentor -> mentor.matchesKeyword(request.keyword()))
                        .map(MentorSearchResponse::from)
                        .collect(Collectors.toUnmodifiableList());

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("카테고리로 멘토를 검색할 수 있다")
    void searchByCategory(){
        MentorSearchRequest request = new MentorSearchRequest(2L, null);

        List<MentorSearchResponse> result = mentorService.searchMentors()
                .stream()
                .filter(mentor -> request.jobCategoryId() == null || mentor.matchesCategory(request.jobCategoryId()))
                .filter(mentor -> mentor.matchesKeyword(request.keyword()))
                .map(MentorSearchResponse::from)
                .collect(Collectors.toUnmodifiableList());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).jobCategoryId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("키워드로 검색할 수 있다")
    void searchByKeyword() {
        MentorSearchRequest request =
                new MentorSearchRequest(
                        null,
                        "Spring"
                );

        List<MentorSearchResponse> result =
                mentorService.searchMentors()
                        .stream()
                        .filter(mentor -> request.jobCategoryId() == null || mentor.matchesCategory(request.jobCategoryId()))
                        .filter(mentor -> mentor.matchesKeyword(request.keyword()))
                        .map(MentorSearchResponse::from)
                        .collect(Collectors.toUnmodifiableList());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).introduction()).contains("Spring");
    }

    @Test
    @DisplayName("조건에 맞는 멘토가 없으면 빈 리스트를 반환한다")
    void returnEmptyList() {
        MentorSearchRequest request =
                new MentorSearchRequest(
                        6L,
                        "Cook"
                );

        List<MentorSearchResponse> result =
                mentorService.searchMentors()
                        .stream()
                        .filter(mentor -> request.jobCategoryId() == null || mentor.matchesCategory(request.jobCategoryId()))
                        .filter(mentor -> mentor.matchesKeyword(request.keyword()))
                        .map(MentorSearchResponse::from)
                        .collect(Collectors.toUnmodifiableList());

        assertThat(result).isEmpty();
    }
}
