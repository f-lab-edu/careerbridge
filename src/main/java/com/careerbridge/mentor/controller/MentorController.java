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

        List<MentorSearchResponse> response = mentorService
                .searchMentors(request.jobCategoryId(), request.keyword())
                .stream()
                .map(MentorSearchResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/mentors/me/profile")
    public ResponseEntity<MentorProfileResponse> createMyProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody MentorProfileRequest request
    ) {
        Mentor saved = mentorService.create(
                authenticatedUser.email(),
                request.companyName(),
                request.position(),
                request.jobCategoryId(),
                request.personalHistory(),
                request.introduction()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MentorProfileResponse.from(saved));
    }

    @PutMapping("/api/mentors/me/profile")
    public ResponseEntity<Void> updateMyProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody MentorProfileRequest request
    ) {
        mentorService.update(
                authenticatedUser.email(),
                request.companyName(),
                request.position(),
                request.jobCategoryId(),
                request.personalHistory(),
                request.introduction()
        );

        return ResponseEntity.noContent().build();
    }
}
