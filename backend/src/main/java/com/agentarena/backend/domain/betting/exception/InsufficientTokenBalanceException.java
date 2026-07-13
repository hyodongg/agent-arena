package com.agentarena.backend.domain.betting.exception;

import com.agentarena.backend.common.exception.BusinessException;
import com.agentarena.backend.common.exception.ErrorCode;

public class InsufficientTokenBalanceException extends BusinessException {

    public InsufficientTokenBalanceException() {
        super(ErrorCode.INSUFFICIENT_TOKEN_BALANCE);
    }
}
