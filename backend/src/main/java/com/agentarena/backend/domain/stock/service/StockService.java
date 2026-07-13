package com.agentarena.backend.domain.stock.service;

import com.agentarena.backend.domain.stock.dto.StockCreateRequest;
import com.agentarena.backend.domain.stock.dto.StockResponse;
import java.util.List;

public interface StockService {

    StockResponse create(StockCreateRequest request);

    List<StockResponse> findAll();

    StockResponse findById(Long id);
}
