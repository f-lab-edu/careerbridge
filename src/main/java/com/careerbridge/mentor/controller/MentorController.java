package com.careerbridge.mentor.controller;

import com.careerbridge.jobcategory.domain.JobCategory;
import com.careerbridge.mentor.dto.MentorSearchRequest;
import com.careerbridge.mentor.dto.MentorSearchResponse;
import com.careerbridge.mentor.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    @GetMapping("/api/mentors")
    public List<MentorSearchResponse> searchMentors(
            @RequestParam(required = false) Long jobCategoryId,
            @RequestParam(required = false) String keyword
    ) {
        MentorSearchRequest request = new MentorSearchRequest(
                jobCategoryId, keyword);

        return mentorService.searchMentors(request);
    }
}
