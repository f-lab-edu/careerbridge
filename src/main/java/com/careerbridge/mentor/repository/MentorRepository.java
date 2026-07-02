package com.careerbridge.mentor.repository;

import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorRepository  {
    List<Mentor> search(Long jobCategoryId, String keyword);

    Optional<Mentor> findByUser(User user);

    Optional<Mentor> findById(Long id);

    boolean existsByUserId(Long id);

    Mentor save(Mentor mentor);

    boolean existsByUserEmail(String email);

}
