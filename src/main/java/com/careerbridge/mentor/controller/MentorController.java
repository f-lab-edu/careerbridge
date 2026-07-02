package com.careerbridge.mentor.controller;

import com.careerbridge.global.security.AuthenticatedUser;
import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.jobcategory.repository.JobCategoryRepository;
import com.careerbridge.mentor.dto.MentorProfileRequest;
import com.careerbridge.mentor.dto.MentorProfileResponse;
import com.careerbridge.mentor.dto.MentorSearchRequest;
import com.careerbridge.mentor.dto.MentorSearchResponse;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.service.MentorService;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserStatus;
import com.careerbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;
    private final UserRepository userRepository;
    private final JobCategoryRepository jobCategoryRepository;

    @GetMapping("/api/mentors")
    public ResponseEntity<List<MentorSearchResponse>> searchMentors(
            @RequestParam(required = false) Long jobCategoryId,
            @RequestParam(required = false) String keyword
    ) {
        MentorSearchRequest request = new MentorSearchRequest(jobCategoryId, keyword);

        List<MentorSearchResponse> response = mentorService.searchMentors()
                .stream()
                .filter(Mentor::isSearchable)
                .filter(mentor -> request.jobCategoryId() == null || mentor.matchesCategory(request.jobCategoryId()))
                .filter(mentor -> mentor.matchesKeyword(request.keyword()))
                .map(MentorSearchResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/mentors/me/profile")
    public ResponseEntity<MentorProfileResponse> createMyProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody MentorProfileRequest request
    ) {
        Mentor mentor = toMentor(authenticatedUser.email(), request);
        Mentor saved = mentorService.create(mentor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MentorProfileResponse.from(saved));
    }

    @PutMapping("/api/mentors/me/profile")
    public ResponseEntity<Void> updateMyProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody MentorProfileRequest request
    ) {
        Mentor mentor = toMentor(authenticatedUser.email(), request);
        mentorService.update(mentor);

        return ResponseEntity.noContent().build();
    }

    private Mentor toMentor(String email, MentorProfileRequest request) {
        User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        JobCategory jobCategory = jobCategoryRepository.findById(request.jobCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직무 카테고리입니다."));

        return Mentor.create(
                null,
                user,
                request.companyName(),
                request.position(),
                jobCategory,
                request.personalHistory(),
                request.introduction()
        );
    }
}
