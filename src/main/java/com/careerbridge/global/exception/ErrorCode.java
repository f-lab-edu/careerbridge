package com.careerbridge.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "Email is already in use."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User was not found."),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "Invalid email or password."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "Unauthorized access."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Invalid request."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
