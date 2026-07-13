package com.agentarena.backend.domain.news.controller;

import com.agentarena.backend.domain.news.dto.NewsCreateRequest;
import com.agentarena.backend.domain.news.dto.NewsResponse;
import com.agentarena.backend.domain.news.service.NewsService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewsResponse create(@Valid @RequestBody NewsCreateRequest request) {
        return newsService.create(request);
    }

    @GetMapping
    public List<NewsResponse> findInjected() {
        return newsService.findInjected();
    }

    @GetMapping("/{id}")
    public NewsResponse findById(@PathVariable Long id) {
        return newsService.findById(id);
    }
}
