package com.careerbridge.mentor.service;

import com.careerbridge.global.security.AuthenticatedUser;
import com.careerbridge.mentor.dto.MentorProfileRequest;
import com.careerbridge.mentor.dto.MentorProfileResponse;
import com.careerbridge.mentor.dto.MentorSearchRequest;
import com.careerbridge.mentor.dto.MentorSearchResponse;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.repository.MentorRepository;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import com.careerbridge.user.entity.UserStatus;
import com.careerbridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorService {

    private final MentorRepository mentorRepository;

    private final UserRepository userRepository;

    public List<MentorSearchResponse> searchMentors(MentorSearchRequest request){
        return mentorRepository.findAll()
                .stream()
                .filter(Mentor::isSearchable)
                .filter(mentor -> mentor.matchesCategory(request.jobCategory()))
                .filter(mentor -> mentor.matchesKeyword(request.keyword()))
                .map(MentorSearchResponse::from)
                .collect(Collectors.toUnmodifiableList());
    }

    public MentorProfileResponse create(User user, MentorProfileRequest request){
        if(user.getRole() != UserRole.MENTOR){
            throw new IllegalArgumentException("멘토만 프로필을 등록할 수 있습니다");
        }
        if(mentorRepository.existsByUserEmail(user.getEmail())){
            throw new IllegalArgumentException("이미 멘토 프로필이 존재합나다");
        }

        Mentor mentor = Mentor.create(null,
                                        user,
                                        request.companyName(),
                                        request.position(),
                                        request.jobCategory(),
                                        request.personalHistory(),
                                        request.introduction());
        Mentor saved = mentorRepository.save(mentor);

        return MentorProfileResponse.from(saved);

    }

    @Transactional
    public MentorProfileResponse update(AuthenticatedUser authenticatedUser, MentorProfileRequest request){

        if(authenticatedUser.role() != UserRole.MENTOR){
            throw new IllegalArgumentException("멘토 프로필만 수정할 수 있습니다.");
        }

        User user = userRepository.findByEmailAndStatus(authenticatedUser.email(), UserStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));

        Mentor mentor = mentorRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("본인 프로필만 수정할 수 있습니다."));

        mentor.update(
                request.companyName(),
                request.position(),
                request.jobCategory(),
                request.personalHistory(),
                request.introduction()
        );

        return MentorProfileResponse.from(mentor);
    }
}
