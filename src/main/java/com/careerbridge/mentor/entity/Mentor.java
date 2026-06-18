package com.careerbridge.mentor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Entity
@Table(name = "mentor")
@Data
public class Mentor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String mentorName;

    @Column(nullable = false)
    @NotBlank
    private String companyName;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String jobCategory;

    @Column(nullable = false)
    private int personalHistory;

    @Column(nullable = false)
    private String introduction;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @Enumerated(EnumType.STRING)
    private VisibilityStatus visibilityStatus;

    protected Mentor() {

    }

    public Mentor(Long id, String mentorName, String companyName,
                  String position, String jobCategory, int personalHistory,
                  String introduction, VerificationStatus verificationStatus, VisibilityStatus visibilityStatus){
        this.id = id;
        this.mentorName = mentorName;
        this.companyName = companyName;
        this.position = position;
        this.jobCategory = jobCategory;
        this.personalHistory = personalHistory;
        this.introduction = introduction;
        this.verificationStatus = verificationStatus;
        this.visibilityStatus = visibilityStatus;
    }

    public boolean isSearchable() {
        return verificationStatus == VerificationStatus.APPROVED &&
                visibilityStatus == VisibilityStatus.PUBLIC;
    }

    public boolean matchesCategory(String jobCategory) {
        if (jobCategory == null || jobCategory.isBlank()) {
            return true;
        }
        return this.jobCategory.equalsIgnoreCase(jobCategory);
    }

    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        String lowerKeyword = keyword.toLowerCase();

        return mentorName.toLowerCase().contains(lowerKeyword)
                || companyName.toLowerCase().contains(lowerKeyword)
                || position.toLowerCase().contains(lowerKeyword)
                || introduction.toLowerCase().contains(lowerKeyword);
    }

    public boolean hasMinimunExperience(Integer minYearsOfExperience) {
        if (minYearsOfExperience == null) {
            return true;
        }
        return this.personalHistory >= minYearsOfExperience;
    }

}
