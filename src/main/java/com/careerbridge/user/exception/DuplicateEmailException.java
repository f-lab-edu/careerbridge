package com.careerbridge.user.exception;

import com.careerbridge.global.exception.BusinessException;
import com.careerbridge.global.exception.ErrorCode;

public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException() {
        super(ErrorCode.DUPLICATE_EMAIL);
    }
}
