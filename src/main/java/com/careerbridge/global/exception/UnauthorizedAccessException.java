package com.careerbridge.global.exception;

public class UnauthorizedAccessException extends BusinessException {

    public UnauthorizedAccessException() {
        super(ErrorCode.UNAUTHORIZED_ACCESS);
    }
}
