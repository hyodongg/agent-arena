package com.agentarena.backend.common.client.kis;

import java.math.BigDecimal;

public record KisQuote(BigDecimal currentPrice, BigDecimal volume) {
}
