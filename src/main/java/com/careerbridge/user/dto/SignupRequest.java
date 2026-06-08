package com.careerbridge.user.dto;

import com.careerbridge.user.entity.UserRole;
import jakarta.validation.constraints.*;

public record SignupRequest(
        @Email(message = "올바르지 않은 이메일 포멧입니다.")
        @NotBlank(message = "이메일을 입력하세요")
        String email,

        @NotBlank(message = "비밀번호를 입력하세요")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).*$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "이름을 입력하세요")
        String name,

        @NotNull(message = "Role is required.")
        UserRole role
) {
}
