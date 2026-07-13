package com.agentarena.backend.domain.news.exception;

import com.agentarena.backend.common.exception.BusinessException;
import com.agentarena.backend.common.exception.ErrorCode;

public class NewsNotFoundException extends BusinessException {

    public NewsNotFoundException() {
        super(ErrorCode.NEWS_NOT_FOUND);
    }
}
