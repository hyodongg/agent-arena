package com.agentarena.backend.domain.stock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Test
    void 주식을_저장하고_코드로_조회할_수_있다() {
        Stock stock = Stock.builder()
                .code("AAA")
                .name("가상전자")
                .currentPrice(new BigDecimal("50000.0000"))
                .volume(1000L)
                .build();

        Stock saved = stockRepository.save(stock);

        Optional<Stock> found = stockRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("AAA", found.get().getCode());
        assertEquals(0, new BigDecimal("50000.0000").compareTo(found.get().getCurrentPrice()));
    }
}
