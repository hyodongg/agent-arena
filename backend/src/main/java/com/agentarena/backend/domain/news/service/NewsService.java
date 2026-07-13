package com.agentarena.backend.domain.news.service;

import com.agentarena.backend.domain.news.dto.NewsCreateRequest;
import com.agentarena.backend.domain.news.dto.NewsResponse;
import java.util.List;

public interface NewsService {

    NewsResponse create(NewsCreateRequest request);

    List<NewsResponse> findInjected();

    NewsResponse findById(Long id);

    void injectNext();
}
