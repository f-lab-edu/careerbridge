package com.careerbridge.mentor.entity;

import com.careerbridge.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Entity
@Table(name = "mentor")
@Getter
public class Mentor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String companyName;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String jobCategory;

    @Column(nullable = false)
    private Integer personalHistory;

    @Column(nullable = false)
    private String introduction;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @Enumerated(EnumType.STRING)
    private VisibilityStatus visibilityStatus;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    protected Mentor() {

    }

    public Mentor(Long id, User user, String companyName,
                  String position, String jobCategory, int personalHistory,
                  String introduction, VerificationStatus verificationStatus, VisibilityStatus visibilityStatus){
        this.id = id;
        this.user = user;
        this.companyName = companyName;
        this.position = position;
        this.jobCategory = jobCategory;
        this.personalHistory = personalHistory;
        this.introduction = introduction;
        this.verificationStatus = verificationStatus;
        this.visibilityStatus = visibilityStatus;
    }

    public String getMentorName() {
        return user.getName();
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

        return getMentorName().toLowerCase().contains(lowerKeyword)
                || companyName.toLowerCase().contains(lowerKeyword)
                || position.toLowerCase().contains(lowerKeyword)
                || introduction.toLowerCase().contains(lowerKeyword);
    }


}
