package com.careerbridge.auth.service;

import com.careerbridge.auth.dto.LoginRequest;
import com.careerbridge.auth.dto.LoginResponse;
import com.careerbridge.auth.exception.InvalidLoginException;
import com.careerbridge.global.security.JwtTokenProvider;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import com.careerbridge.user.entity.UserStatus;
import com.careerbridge.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private JwtTokenProvider jwtTokenProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "careerbridge-test-secret-key-must-be-long-enough",
                3600000L,
                new ObjectMapper()
        );
        authService = new AuthService(userRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    void loginReturnsBearerAccessToken() {
        User user = User.create(
                "mentee@example.com",
                "encoded-password",
                "Kim Mentee",
                UserRole.MENTEE
        );
        when(userRepository.findByEmailAndStatus("mentee@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain-password", "encoded-password")).thenReturn(true);

        LoginResponse response = authService.login(new LoginRequest("mentee@example.com", "plain-password"));

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isNotBlank();
        assertThat(jwtTokenProvider.extractEmail(response.accessToken())).isEqualTo("mentee@example.com");
        assertThat(jwtTokenProvider.extractRole(response.accessToken())).isEqualTo(UserRole.MENTEE);
    }

    @Test
    void loginRejectsUnknownEmail() {
        when(userRepository.findByEmailAndStatus("unknown@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("unknown@example.com", "plain-password")))
                .isInstanceOf(InvalidLoginException.class)
                .hasMessage("Invalid email or password.");
    }

    @Test
    void loginRejectsWrongPassword() {
        User user = User.create(
                "mentee@example.com",
                "encoded-password",
                "Kim Mentee",
                UserRole.MENTEE
        );
        when(userRepository.findByEmailAndStatus("mentee@example.com", UserStatus.ACTIVE))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("mentee@example.com", "wrong-password")))
                .isInstanceOf(InvalidLoginException.class)
                .hasMessage("Invalid email or password.");
    }
}
