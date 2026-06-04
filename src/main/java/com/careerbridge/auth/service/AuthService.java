package com.careerbridge.auth.service;

import com.careerbridge.auth.dto.LoginRequest;
import com.careerbridge.auth.dto.LoginResponse;
import com.careerbridge.auth.exception.InvalidLoginException;
import com.careerbridge.global.security.JwtTokenProvider;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserStatus;
import com.careerbridge.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndStatus(request.email(), UserStatus.ACTIVE)
                .orElseThrow(InvalidLoginException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidLoginException();
        }

        return LoginResponse.bearer(jwtTokenProvider.createAccessToken(user));
    }
}
