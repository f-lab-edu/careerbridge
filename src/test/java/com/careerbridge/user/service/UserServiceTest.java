package com.careerbridge.user.service;

import com.careerbridge.user.dto.SignupRequest;
import com.careerbridge.user.dto.UserResponse;
import com.careerbridge.user.entity.User;
import com.careerbridge.user.entity.UserRole;
import com.careerbridge.user.entity.UserStatus;
import com.careerbridge.user.exception.DuplicateEmailException;
import com.careerbridge.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void signupCreatesActiveUserWithEncodedPassword() {
        SignupRequest request = new SignupRequest(
                "mentee@example.com",
                "plain-password",
                "Kim Mentee",
                UserRole.MENTEE
        );
        when(userRepository.existsByEmail("mentee@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.signup(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getEmail()).isEqualTo("mentee@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
        assertThat(savedUser.getName()).isEqualTo("Kim Mentee");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.MENTEE);
        assertThat(savedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(response.email()).isEqualTo("mentee@example.com");
        assertThat(response.role()).isEqualTo(UserRole.MENTEE);
    }

    @Test
    void signupRejectsDuplicateEmail() {
        SignupRequest request = new SignupRequest(
                "mentee@example.com",
                "plain-password",
                "Kim Mentee",
                UserRole.MENTEE
        );
        when(userRepository.existsByEmail("mentee@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void findActiveUserByEmailReturnsOnlyActiveUser() {
        User user = User.create(
                "mentor@example.com",
                "encoded-password",
                "Park Mentor",
                UserRole.MENTOR
        );
        when(userRepository.findByEmailAndStatus(anyString(), org.mockito.ArgumentMatchers.eq(UserStatus.ACTIVE)))
                .thenReturn(Optional.of(user));

        User foundUser = userService.findActiveUserByEmail("mentor@example.com");

        assertThat(foundUser.getEmail()).isEqualTo("mentor@example.com");
        assertThat(foundUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
