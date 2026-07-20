package com.agentarena.backend.domain.news.service;

import com.agentarena.backend.common.client.naver.NaverNewsArticle;
import com.agentarena.backend.common.client.naver.NaverNewsClient;
import com.agentarena.backend.domain.news.News;
import com.agentarena.backend.domain.news.repository.NewsRepository;
import com.agentarena.backend.domain.stock.Stock;
import com.agentarena.backend.domain.stock.repository.StockRepository;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsCollectionServiceImpl implements NewsCollectionService {

    private static final int SEARCH_RESULT_COUNT = 5;

    private final StockRepository stockRepository;
    private final NewsRepository newsRepository;
    private final NaverNewsClient naverNewsClient;
    private final NewsClassifier newsClassifier;

    /** 종목 라운드로빈 커서. 재시작하면 처음으로 돌아가지만 무해하다. */
    private final AtomicInteger cursor = new AtomicInteger(0);

    @Override
    @Transactional
    public void collectNext() {
        List<Stock> stocks = stockRepository.findAll();
        if (stocks.isEmpty()) {
            return;
        }

        Stock stock = stocks.get(Math.floorMod(cursor.getAndIncrement(), stocks.size()));

        try {
            collectFor(stock);
        } catch (Exception e) {
            // 다음 사이클에 같은 종목이 다시 돌아오므로 재시도 로직을 따로 두지 않는다.
            log.warn("뉴스 수집 실패 (종목 {} {}): {}", stock.getCode(), stock.getName(), e.getMessage());
        }
    }

    private void collectFor(Stock stock) {
        List<NaverNewsArticle> candidates = naverNewsClient
                .search(stock.getName() + " 주가", SEARCH_RESULT_COUNT)
                .stream()
                .filter(article -> !newsRepository.existsBySourceUrl(article.sourceUrl()))
                .toList();

        if (candidates.isEmpty()) {
            log.debug("뉴스 수집: {} 신규 기사 없음", stock.getName());
            return;
        }

        List<NewsVerdict> verdicts = newsClassifier.classify(stock.getName(), candidates);

        int saved = 0;
        for (NewsVerdict verdict : verdicts) {
            if (!verdict.isTradeable()) {
                continue;
            }
            NaverNewsArticle article = candidates.get(verdict.index());
            newsRepository.save(
                    News.builder()
                            .relatedStock(stock)
                            .title(article.title())
                            .sourceUrl(article.sourceUrl())
                            .sentiment(verdict.sentiment())
                            .publishedAt(article.publishedAt())
                            .build()
            );
            saved++;
        }

        log.info("뉴스 수집: {} 후보 {}건 중 {}건 저장", stock.getName(), candidates.size(), saved);
    }
}
