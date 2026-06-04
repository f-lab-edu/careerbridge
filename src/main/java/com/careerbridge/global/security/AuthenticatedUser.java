package com.careerbridge.global.security;

import com.careerbridge.user.entity.UserRole;

public record AuthenticatedUser(
        String email,
        UserRole role
) {
}
