package com.careerbridge.mentee.controller;

import com.careerbridge.global.response.ApiResponse;
import com.careerbridge.global.security.AuthenticatedUser;
import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.jobcategory.service.JobCategoryService;
import com.careerbridge.mentee.dto.MenteeProfileRequest;
import com.careerbridge.mentee.dto.MenteeProfileResponse;
import com.careerbridge.mentee.entity.Mentee;
import com.careerbridge.mentee.service.MenteeService;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mentees/me/profile")
public class MenteeController {

    private final MenteeService menteeService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<MenteeProfileResponse>> createMyProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody MenteeProfileRequest request
    ) {
        User user = userService.findActiveUserByEmail(authenticatedUser.email());
        Mentee saved = menteeService.create(user, request.jobCategoryIdList());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Mentee profile created.",
                        MenteeProfileResponse.from(saved)
                ));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Void>> updateMyProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody MenteeProfileRequest request
    ) {
        User user = userService.findActiveUserByEmail(authenticatedUser.email());
        menteeService.update(user, request.jobCategoryIdList());

        return ResponseEntity.ok(ApiResponse.success("Mentee profile updated.", null));
    }

}
