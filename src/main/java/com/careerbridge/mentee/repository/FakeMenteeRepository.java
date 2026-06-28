package com.careerbridge.mentee.repository;

import com.careerbridge.mentee.entity.Mentee;
import com.careerbridge.mentee.entity.MenteeJobCategory;

import java.util.ArrayList;
import java.util.List;

public class FakeMenteeRepository implements MenteeRepository{
    private Long nextId = 1L;
    private final List<Mentee> mentees = new ArrayList<>();

    @Override
    public boolean existsByUserId(Long id) {
        return mentees.stream()
                .anyMatch(mentee -> mentee.getUser().getId().equals(id));
    }

    @Override
    public boolean existsByUserEmail(String email) {
        return mentees.stream()
                .anyMatch(mentee -> mentee.getUser().getEmail().equals(email));
    }

    @Override
    public Mentee save(Mentee mentee) {
        Mentee saved = Mentee.create(
                nextId++,
                mentee.getUser(),
                mentee.getJobCategories().stream()
                        .map(MenteeJobCategory::getJobCategory)
                        .toList()
        );
        mentees.add(saved);
        return saved;
    }
}
