package com.careerbridge.mentor.repository;

import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.entity.VerificationStatus;
import com.careerbridge.mentor.entity.VisibilityStatus;

import java.util.List;

public class FakeMentorRepository implements MentorRepository {
    private final List<Mentor> mentors = List.of(
            new Mentor(
                    1L,
                    "김백엔드",
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
                    "이프론트",
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
                    "박데브옵스",
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
                    "비공개멘토",
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
                    "승인대기멘토",
                    "라인",
                    "Backend Developer",
                    "Backend",
                    3,
                    "아직 승인되지 않은 멘토입니다.",
                    VerificationStatus.PENDING,
                    VisibilityStatus.PUBLIC
            )
    );

    @Override
    public List<Mentor> findAll() {
        return mentors;
    }
}
