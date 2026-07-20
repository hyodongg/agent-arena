package com.agentarena.backend.domain.news.scheduler;

import com.agentarena.backend.domain.news.service.NewsCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsCollectionScheduler {

    private final NewsCollectionService newsCollectionService;

    /**
     * 60초마다 종목 하나씩 수집한다. 30종목이면 한 바퀴에 30분이다.
     *
     * <p>주입(5초)보다 수집이 훨씬 느린 건 의도한 것이다. 실제 뉴스가 그렇게 자주 나오지 않는다.
     */
    @Scheduled(fixedDelay = 60000)
    public void collectNews() {
        newsCollectionService.collectNext();
    }
}
