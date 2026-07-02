package com.careerbridge.mentor.repository;

import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.entity.VerificationStatus;
import com.careerbridge.mentor.entity.VisibilityStatus;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FakeMentorRepository implements MentorRepository {

    private Long nextId = 6L;

    private final List<Mentor> mentors = new ArrayList<>(List.of(
            new Mentor(
                    1L,
                    createMentorUser("mentor1@example.com", "김백엔드"),
                    "네이버",
                    "Backend Developer",
                    "Backend",
                    7,
                    "Spring Boot와 대용량 트래픽 경험이 있습니다.",
                    VerificationStatus.APPROVED,
                    VisibilityStatus.PUBLIC
            ),
            new Mentor(
                    2L,
                    createMentorUser("mentor2@example.com", "이프론트"),
                    "카카오",
                    "Frontend Developer",
                    "Frontend",
                    5,
                    "React 기반 서비스 개발 경험이 있습니다.",
                    VerificationStatus.APPROVED,
                    VisibilityStatus.PUBLIC
            ),
            new Mentor(
                    3L,
                    createMentorUser("mentor3@example.com", "박데브옵스"),
                    "쿠팡",
                    "DevOps Engineer",
                    "DevOps",
                    6,
                    "AWS, Docker, Kubernetes 경험이 있습니다.",
                    VerificationStatus.APPROVED,
                    VisibilityStatus.PUBLIC
            ),
            new Mentor(
                    4L,
                    createMentorUser("mentor4@example.com", "비공개멘토"),
                    "토스",
                    "Backend Developer",
                    "Backend",
                    10,
                    "비공개 멘토입니다.",
                    VerificationStatus.APPROVED,
                    VisibilityStatus.PRIVATE
            ),
            new Mentor(
                    5L,
                    createMentorUser("mentor5@example.com", "미승인멘토"),
                    "라인",
                    "Backend Developer",
                    "Backend",
                    3,
                    "아직 승인되지 않은 멘토입니다.",
                    VerificationStatus.PENDING,
                    VisibilityStatus.PUBLIC
            )
        )
    );

    @Override
    public List<Mentor> findAll() {
        return List.of();
    }

    @Override
    public List<Mentor> search(Long jobCategoryId, String keyword) {
        return null;
    }

    @Override
    public Optional<Mentor> findByUser(User user) {
        return mentors.stream()
                .filter(mentor -> mentor.getUser().getEmail().equals(user.getEmail()))
                .findFirst();
    }

    @Override
    public Optional<Mentor> findById(Long id) {
        return mentors.stream().filter(mentor -> mentor.getId().equals(id))
                .findFirst();
    }

    @Override
    public boolean existsByUserId(Long id) {
        return mentors.stream()
                .anyMatch(mentor -> mentor.getUser().getId().equals(id));
    }

    @Override
    public Mentor save(Mentor mentor) {
        Mentor saved = Mentor.create(
                nextId++,
                mentor.getUser(),
                mentor.getCompanyName(),
                mentor.getPosition(),
                mentor.getJobCategory(),
                mentor.getPersonalHistory(),
                mentor.getIntroduction());

        mentors.add(saved);
        return saved;
    }

    @Override
    public boolean existsByUserEmail(String email) {
        return mentors.stream()
                .anyMatch(mentor -> mentor.getUser().getEmail().equals(email));
    }


    private static User createMentorUser(String email, String name) {
        return User.create(
                email,
                "FakePassword1!",
                name,
                UserRole.MENTOR
        );
    }
}
