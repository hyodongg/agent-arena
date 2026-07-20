package com.agentarena.backend.domain.stock.service;

import com.agentarena.backend.common.client.kis.KisClient;
import com.agentarena.backend.common.client.kis.KisQuote;
import com.agentarena.backend.domain.stock.Stock;
import com.agentarena.backend.domain.stock.repository.StockRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockPriceSyncServiceImpl implements StockPriceSyncService {

    private static final ZoneId KRX_ZONE = ZoneId.of("Asia/Seoul");
    private static final LocalTime MARKET_OPEN = LocalTime.of(9, 0);
    private static final LocalTime MARKET_CLOSE = LocalTime.of(15, 30);

    /** KIS 모의투자 시세 조회는 초당 2~3건이 한계라 실측됐다. 초당 2건으로 맞춘다. */
    private static final long THROTTLE_MILLIS = 500L;

    private final StockRepository stockRepository;
    private final StockMarketDataWriter stockMarketDataWriter;
    private final KisClient kisClient;

    @Override
    public void syncAll() {
        if (!isMarketOpen()) {
            return;
        }

        List<Stock> stocks = stockRepository.findAll();
        int synced = 0;
        int failed = 0;

        for (Stock stock : stocks) {
            try {
                KisQuote quote = kisClient.fetchQuote(stock.getCode());
                stockMarketDataWriter.write(stock.getId(), quote);
                synced++;
            } catch (Exception e) {
                // 개별 종목 실패는 마지막 값을 유지한 채 넘어간다. 한 종목 때문에 사이클 전체를 버리지 않는다.
                failed++;
                log.warn("시세 동기화 실패 (종목 {} {}): {}", stock.getCode(), stock.getName(), e.getMessage());
            }

            if (!sleepBetweenCalls()) {
                log.warn("시세 동기화 사이클이 중단됐다. 동기화 {}건, 실패 {}건", synced, failed);
                return;
            }
        }

        log.info("시세 동기화 완료. 동기화 {}건, 실패 {}건", synced, failed);
    }

    private boolean isMarketOpen() {
        ZonedDateTime now = ZonedDateTime.now(KRX_ZONE);
        if (now.getDayOfWeek() == DayOfWeek.SATURDAY || now.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }
        LocalTime time = now.toLocalTime();
        return !time.isBefore(MARKET_OPEN) && !time.isAfter(MARKET_CLOSE);
    }

    /** @return 계속 진행해도 되면 true, 인터럽트로 중단해야 하면 false */
    private boolean sleepBetweenCalls() {
        try {
            Thread.sleep(THROTTLE_MILLIS);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
