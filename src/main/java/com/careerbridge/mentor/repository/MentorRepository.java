package com.careerbridge.mentor.repository;

import com.careerbridge.mentor.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorRepository  {
    List<Mentor> findAll();
}
