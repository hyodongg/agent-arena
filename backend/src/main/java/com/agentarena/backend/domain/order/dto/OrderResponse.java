package com.agentarena.backend.domain.order.dto;

import com.agentarena.backend.domain.order.Order;
import com.agentarena.backend.domain.order.OrderType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long agentId,
        String agentName,
        Long stockId,
        String stockCode,
        OrderType type,
        Long quantity,
        BigDecimal price,
        LocalDateTime executedAt
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getAgent().getId(),
                order.getAgent().getName(),
                order.getStock().getId(),
                order.getStock().getCode(),
                order.getType(),
                order.getQuantity(),
                order.getPrice(),
                order.getExecutedAt()
        );
    }
}
