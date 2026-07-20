package com.agentarena.backend.domain.stock.service;

import com.agentarena.backend.common.client.kis.KisQuote;
import com.agentarena.backend.domain.stock.Stock;
import com.agentarena.backend.domain.stock.exception.StockNotFoundException;
import com.agentarena.backend.domain.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 시세 동기화 결과를 종목 하나 단위로 커밋한다.
 *
 * <p>한 바퀴(30종목, 약 15초) 전체를 한 트랜잭션으로 묶으면 그동안 커넥션과 행 잠금을 붙들고 있어
 * 뉴스 주입 트랜잭션과 충돌한다. 그래서 별도 빈으로 분리해 종목마다 트랜잭션을 끊는다.
 */
@Component
@RequiredArgsConstructor
public class StockMarketDataWriter {

    private final StockRepository stockRepository;

    @Transactional
    public void write(Long stockId, KisQuote quote) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(StockNotFoundException::new);
        stock.syncMarketData(quote.currentPrice(), quote.volume());
    }
}
