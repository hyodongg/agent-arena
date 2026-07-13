package com.agentarena.backend.domain.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record StockCreateRequest(
        @NotBlank(message = "종목 코드를 입력해주세요.") String code,
        @NotBlank(message = "종목명을 입력해주세요.") String name,
        @NotNull(message = "현재가를 입력해주세요.") @Positive(message = "현재가는 0보다 커야 합니다.") BigDecimal currentPrice,
        @NotNull(message = "거래량을 입력해주세요.") @PositiveOrZero(message = "거래량은 0 이상이어야 합니다.") BigDecimal volume
) {
}
