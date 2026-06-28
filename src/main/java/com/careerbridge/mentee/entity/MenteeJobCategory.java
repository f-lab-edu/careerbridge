package com.careerbridge.mentee.entity;

import com.careerbridge.jobcategory.domain.JobCategory;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
@Table(name = "mentee_job_category", uniqueConstraints = {
        @UniqueConstraint(name = "uk_mentee_job_category",
        columnNames = {"mentee_id", "job_category_id"}
        )
    }
)
public class MenteeJobCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private Mentee mentee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_category_id", nullable = false)
    private JobCategory jobCategory;

    protected MenteeJobCategory() {

    }

    public MenteeJobCategory(Long id, Mentee mentee, JobCategory jobCategory) {
        this.id = id;
        this.mentee = mentee;
        this.jobCategory = jobCategory;
    }

    public static MenteeJobCategory create( Mentee mentee, JobCategory jobCategory){
        return new MenteeJobCategory(null, mentee,jobCategory);
    }
}
