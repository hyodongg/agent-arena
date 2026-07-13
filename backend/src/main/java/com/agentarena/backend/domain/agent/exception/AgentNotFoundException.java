package com.agentarena.backend.domain.agent.exception;

import com.agentarena.backend.common.exception.BusinessException;
import com.agentarena.backend.common.exception.ErrorCode;

public class AgentNotFoundException extends BusinessException {

    public AgentNotFoundException() {
        super(ErrorCode.AGENT_NOT_FOUND);
    }
}
