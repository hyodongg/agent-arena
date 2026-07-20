package com.agentarena.backend.domain.news.service;

import com.agentarena.backend.domain.news.News;
import com.agentarena.backend.domain.news.dto.NewsCreateRequest;
import com.agentarena.backend.domain.news.dto.NewsResponse;
import com.agentarena.backend.domain.news.exception.NewsNotFoundException;
import com.agentarena.backend.domain.news.repository.NewsRepository;
import com.agentarena.backend.domain.order.service.OrderService;
import com.agentarena.backend.domain.stock.Stock;
import com.agentarena.backend.domain.stock.exception.StockNotFoundException;
import com.agentarena.backend.domain.stock.repository.StockRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final StockRepository stockRepository;
    private final OrderService orderService;

    @Override
    @Transactional
    public NewsResponse create(NewsCreateRequest request) {
        Stock relatedStock = stockRepository.findById(request.relatedStockId())
                .orElseThrow(StockNotFoundException::new);

        News news = newsRepository.save(
                News.builder()
                        .relatedStock(relatedStock)
                        .title(request.title())
                        .sourceUrl(request.sourceUrl())
                        .sentiment(request.sentiment())
                        .publishedAt(request.publishedAt())
                        .build()
        );
        return NewsResponse.from(news);
    }

    @Override
    public List<NewsResponse> findInjected() {
        return newsRepository.findByInjectedAtIsNotNullOrderByInjectedAtDesc().stream()
                .map(NewsResponse::from)
                .toList();
    }

    @Override
    public NewsResponse findById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(NewsNotFoundException::new);
        return NewsResponse.from(news);
    }

    @Override
    @Transactional
    public void injectNext() {
        newsRepository.findFirstByInjectedAtIsNullOrderByPublishedAtAsc()
                .ifPresent(news -> {
                    news.inject(LocalDateTime.now());
                    orderService.simulateForNews(news);
                });
    }
}
