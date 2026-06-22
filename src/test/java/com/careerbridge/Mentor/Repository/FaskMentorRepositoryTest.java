package com.careerbridge.Mentor.Repository;

import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.repository.FakeMentorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FaskMentorRepositoryTest {

    private FakeMentorRepository repository;

    @BeforeEach
    void setUp() {
        repository = new FakeMentorRepository();
    }

    @Test
    @DisplayName("멘토 목록을 조회할 수 있다")
    void findAll() {

        // when
        List<Mentor> mentors = repository.findAll();

        // then
        assertThat(mentors).hasSize(5);
    }
}
