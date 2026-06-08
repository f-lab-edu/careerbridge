package com.careerbridge.auth.exception;

import com.careerbridge.global.exception.BusinessException;
import com.careerbridge.global.exception.ErrorCode;

public class InvalidLoginException extends BusinessException {

    public InvalidLoginException() {
        super(ErrorCode.INVALID_LOGIN);
    }
}
