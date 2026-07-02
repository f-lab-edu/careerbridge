package com.careerbridge.mentee.repository;

import com.careerbridge.mentee.entity.Mentee;
import com.careerbridge.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenteeRepository {

    boolean existsByUserId(Long id);

    boolean existsByUserEmail(String email);

    Optional<Mentee> findByUser(User user);

    Mentee save(Mentee mentee);
}
