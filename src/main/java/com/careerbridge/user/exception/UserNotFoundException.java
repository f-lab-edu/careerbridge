package com.careerbridge.user.exception;

import com.careerbridge.global.exception.BusinessException;
import com.careerbridge.global.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
