package com.agentarena.backend.domain.round.exception;

import com.agentarena.backend.common.exception.BusinessException;
import com.agentarena.backend.common.exception.ErrorCode;

public class RoundAlreadySettledException extends BusinessException {

    public RoundAlreadySettledException() {
        super(ErrorCode.ROUND_ALREADY_SETTLED);
    }
}
