package com.agentarena.backend.domain.stock.service;

import com.agentarena.backend.domain.stock.Stock;
import com.agentarena.backend.domain.stock.dto.StockCreateRequest;
import com.agentarena.backend.domain.stock.dto.StockResponse;
import com.agentarena.backend.domain.stock.exception.StockNotFoundException;
import com.agentarena.backend.domain.stock.repository.StockRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    @Transactional
    public StockResponse create(StockCreateRequest request) {
        Stock stock = stockRepository.save(
                Stock.builder()
                        .code(request.code())
                        .name(request.name())
                        .currentPrice(request.currentPrice())
                        .volume(request.volume())
                        .build()
        );
        return StockResponse.from(stock);
    }

    @Override
    public List<StockResponse> findAll() {
        return stockRepository.findAll().stream()
                .map(StockResponse::from)
                .toList();
    }

    @Override
    public StockResponse findById(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(StockNotFoundException::new);
        return StockResponse.from(stock);
    }
}
