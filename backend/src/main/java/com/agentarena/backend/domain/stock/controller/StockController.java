package com.agentarena.backend.domain.stock.controller;

import com.agentarena.backend.domain.stock.dto.StockCreateRequest;
import com.agentarena.backend.domain.stock.dto.StockResponse;
import com.agentarena.backend.domain.stock.service.StockService;
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
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockResponse create(@Valid @RequestBody StockCreateRequest request) {
        return stockService.create(request);
    }

    @GetMapping
    public List<StockResponse> findAll() {
        return stockService.findAll();
    }

    @GetMapping("/{id}")
    public StockResponse findById(@PathVariable Long id) {
        return stockService.findById(id);
    }
}
