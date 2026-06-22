package com.careerbridge.user.controller;

import com.careerbridge.global.security.JwtTokenProvider;
import com.careerbridge.user.dto.SignupRequest;
import com.careerbridge.user.dto.UserResponse;
import com.careerbridge.user.entity.UserRole;
import com.careerbridge.user.entity.UserStatus;
import com.careerbridge.user.exception.DuplicateEmailException;
import com.careerbridge.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void signupReturnsCommonSuccessResponse() throws Exception {
        SignupRequest request = new SignupRequest(
                "mentee@example.com",
                "password123!",
                "Kim Mentee",
                UserRole.MENTEE
        );
        UserResponse response = new UserResponse(
                1L,
                "mentee@example.com",
                "Kim Mentee",
                UserRole.MENTEE,
                UserStatus.ACTIVE
        );
        when(userService.signup(ArgumentMatchers.any(SignupRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Signup succeeded."))
                .andExpect(jsonPath("$.data.email").value("mentee@example.com"))
                .andExpect(jsonPath("$.data.role").value("MENTEE"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    void signupReturnsCommonErrorResponseForDuplicateEmail() throws Exception {
        SignupRequest request = new SignupRequest(
                "mentee@example.com",
                "password123!",
                "Kim Mentee",
                UserRole.MENTEE
        );
        when(userService.signup(ArgumentMatchers.any(SignupRequest.class)))
                .thenThrow(new DuplicateEmailException());

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email is already in use."))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
}
