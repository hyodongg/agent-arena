package com.agentarena.backend.domain.round.exception;

import com.agentarena.backend.common.exception.BusinessException;
import com.agentarena.backend.common.exception.ErrorCode;

public class RoundNotFoundException extends BusinessException {

    public RoundNotFoundException() {
        super(ErrorCode.ROUND_NOT_FOUND);
    }
}
