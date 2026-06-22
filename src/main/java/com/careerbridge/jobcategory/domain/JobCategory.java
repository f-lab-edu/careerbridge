package com.careerbridge.jobcategory.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_category")
@Getter
public class JobCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String jobName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private JobCategory parent;

    @OneToMany(mappedBy = "parent")
    private List<JobCategory> children = new ArrayList<>();

    protected JobCategory(){}

    public JobCategory(Long id, String jobName,  JobCategory parent, List<JobCategory> children){
        this.id = id;
        this.jobName = jobName;
        this.parent = parent;
        this.children = children;
    }

}
