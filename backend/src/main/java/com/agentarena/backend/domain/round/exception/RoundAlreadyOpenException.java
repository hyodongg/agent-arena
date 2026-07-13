package com.agentarena.backend.domain.round.exception;

import com.agentarena.backend.common.exception.BusinessException;
import com.agentarena.backend.common.exception.ErrorCode;

public class RoundAlreadyOpenException extends BusinessException {

    public RoundAlreadyOpenException() {
        super(ErrorCode.ROUND_ALREADY_OPEN);
    }
}
