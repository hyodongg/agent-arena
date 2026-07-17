package com.agentarena.backend.domain.user.exception;

import com.agentarena.backend.common.exception.BusinessException;
import com.agentarena.backend.common.exception.ErrorCode;

public class DuplicateUsernameException extends BusinessException {

    public DuplicateUsernameException() {
        super(ErrorCode.DUPLICATE_USERNAME);
    }
}
