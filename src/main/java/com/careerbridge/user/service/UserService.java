package com.careerbridge.user.service;

import com.careerbridge.user.dto.SignupRequest;
import com.careerbridge.user.dto.UserResponse;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserStatus;
import com.careerbridge.user.exception.DuplicateEmailException;
import com.careerbridge.user.exception.UserNotFoundException;
import com.careerbridge.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.create(request.email(), encodedPassword, request.name(), request.role());
        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    @Transactional(readOnly = true)
    public User findActiveUserByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(UserNotFoundException::new);
    }
}
