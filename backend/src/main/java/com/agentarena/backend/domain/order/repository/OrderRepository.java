package com.agentarena.backend.domain.order.repository;

import com.agentarena.backend.domain.order.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByOrderByExecutedAtDesc();

    List<Order> findByAgent_IdOrderByExecutedAtDesc(Long agentId);
}
