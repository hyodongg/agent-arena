package com.agentarena.backend.domain.betting.exception;

import com.agentarena.backend.common.exception.BusinessException;
import com.agentarena.backend.common.exception.ErrorCode;

public class BettingNotFoundException extends BusinessException {

    public BettingNotFoundException() {
        super(ErrorCode.BETTING_NOT_FOUND);
    }
}
