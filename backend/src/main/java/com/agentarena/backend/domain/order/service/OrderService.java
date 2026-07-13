package com.agentarena.backend.domain.order.service;

import com.agentarena.backend.domain.news.News;
import com.agentarena.backend.domain.order.dto.OrderResponse;
import java.util.List;

public interface OrderService {

    void simulateForNews(News news);

    List<OrderResponse> findAll(Long agentId);

    OrderResponse findById(Long id);
}
