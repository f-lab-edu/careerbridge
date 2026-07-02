package com.careerbridge.mentor.repository;

import com.careerbridge.jobcategory.domain.JobCategory;
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

    private static final JobCategory DEVELOPMENT =
            new JobCategory(1L, "개발", null, new ArrayList<>());

    private static final JobCategory BACKEND =
            new JobCategory(2L, "Backend", DEVELOPMENT, List.of());

    private static final JobCategory FRONTEND =
            new JobCategory(3L, "Frontend", DEVELOPMENT, List.of());

    private static final JobCategory DEVOPS =
            new JobCategory(4L, "DevOps", DEVELOPMENT, List.of());

    private Long nextId = 6L;

    private final List<Mentor> mentors = new ArrayList<>(List.of(
            new Mentor(
                    1L,
                    createMentorUser("mentor1@example.com", "김백엔드"),
                    "네이버",
                    "Backend Developer",
                    BACKEND,
                    7,
                    "Spring Boot 기반 백엔드 개발 경험이 있습니다.",
                    VerificationStatus.APPROVED,
                    VisibilityStatus.PUBLIC
            ),
            new Mentor(
                    2L,
                    createMentorUser("mentor2@example.com", "이프론트"),
                    "카카오",
                    "Frontend Developer",
                    FRONTEND,
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
                    DEVOPS,
                    6,
                    "AWS, Docker, Kubernetes 경험이 있습니다.",
                    VerificationStatus.APPROVED,
                    VisibilityStatus.PUBLIC
            )
    ));

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
        if (mentor.getId() == null) {
            Mentor saved = new Mentor(
                    nextId++,
                    mentor.getUser(),
                    mentor.getCompanyName(),
                    mentor.getPosition(),
                    mentor.getJobCategory(),
                    mentor.getPersonalHistory(),
                    mentor.getIntroduction(),
                    mentor.getVerificationStatus(),
                    mentor.getVisibilityStatus()
            );

            mentors.add(saved);
            return saved;
        }

        mentors.removeIf(existing -> existing.getId().equals(mentor.getId()));
        mentors.add(mentor);

        return mentor;
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
