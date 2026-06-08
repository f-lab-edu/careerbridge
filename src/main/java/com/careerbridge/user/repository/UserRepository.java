package com.careerbridge.user.repository;

import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, UserStatus status);
}
