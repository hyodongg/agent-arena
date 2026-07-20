package com.agentarena.backend.domain.stock.scheduler;

import com.agentarena.backend.domain.stock.service.StockPriceSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockPriceSyncScheduler {

    private final StockPriceSyncService stockPriceSyncService;

    /** 한 바퀴가 끝난 뒤 5초 쉬고 다시 돈다. 장 시간 판별은 서비스가 한다. */
    @Scheduled(fixedDelay = 5000)
    public void syncStockPrices() {
        stockPriceSyncService.syncAll();
    }
}
