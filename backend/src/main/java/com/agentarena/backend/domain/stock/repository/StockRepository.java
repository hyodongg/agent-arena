package com.agentarena.backend.domain.stock.repository;

import com.agentarena.backend.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
