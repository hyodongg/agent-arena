package com.agentarena.backend.domain.news.scheduler;

import com.agentarena.backend.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsInjectionScheduler {

    private final NewsService newsService;

    @Scheduled(fixedRate = 5000)
    public void injectNextNews() {
        newsService.injectNext();
    }
}
