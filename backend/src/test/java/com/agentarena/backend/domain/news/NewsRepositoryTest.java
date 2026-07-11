package com.agentarena.backend.domain.news;

import com.agentarena.backend.domain.stock.Stock;
import com.agentarena.backend.domain.stock.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class NewsRepositoryTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private StockRepository stockRepository;

    @Test
    void 뉴스를_저장하고_조회할_수_있다() {
        Stock stock = stockRepository.save(Stock.builder()
                .code("CCC").name("가상반도체").currentPrice(new BigDecimal("70000.0000")).volume(2000L).build());

        News news = News.builder()
                .relatedStock(stock)
                .title("가상반도체, 깜짝 실적 발표")
                .sentiment(NewsSentiment.POSITIVE)
                .publishedAt(LocalDateTime.now())
                .build();

        News saved = newsRepository.save(news);

        Optional<News> found = newsRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(NewsSentiment.POSITIVE, found.get().getSentiment());
        assertEquals(stock.getId(), found.get().getRelatedStock().getId());
    }
}
