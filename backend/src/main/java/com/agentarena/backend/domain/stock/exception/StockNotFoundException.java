package com.agentarena.backend.domain.stock.exception;

import com.agentarena.backend.common.exception.BusinessException;
import com.agentarena.backend.common.exception.ErrorCode;

public class StockNotFoundException extends BusinessException {

    public StockNotFoundException() {
        super(ErrorCode.STOCK_NOT_FOUND);
    }
}
