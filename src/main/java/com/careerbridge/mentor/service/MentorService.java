package com.careerbridge.mentor.service;

import com.careerbridge.mentor.dto.MentorSearchRequest;
import com.careerbridge.mentor.dto.MentorSearchResponse;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorService {

    private final MentorRepository mentorRepository;

    public List<MentorSearchResponse> searchMentors(MentorSearchRequest request){
        return mentorRepository.findAll()
                .stream()
                .filter(Mentor::isSearchable)
                .filter(mentor -> mentor.matchesCategory(request.jobCategory()))
                .filter(mentor -> mentor.matchesKeyword(request.keyword()))
                .map(MentorSearchResponse::from)
                .collect(Collectors.toUnmodifiableList());
    }
}
