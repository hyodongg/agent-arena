package com.agentarena.backend.domain.user.exception;

import com.agentarena.backend.common.exception.BusinessException;
import com.agentarena.backend.common.exception.ErrorCode;

public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS);
    }
}
