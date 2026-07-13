package com.agentarena.backend.domain.stock.dto;

import com.agentarena.backend.domain.stock.Stock;
import java.math.BigDecimal;

public record StockResponse(Long id, String code, String name, BigDecimal currentPrice, Long volume) {

    public static StockResponse from(Stock stock) {
        return new StockResponse(
                stock.getId(),
                stock.getCode(),
                stock.getName(),
                stock.getCurrentPrice(),
                stock.getVolume()
        );
    }
}
