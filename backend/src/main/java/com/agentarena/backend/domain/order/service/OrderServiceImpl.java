package com.agentarena.backend.domain.order.service;

import com.agentarena.backend.domain.agent.Agent;
import com.agentarena.backend.domain.agent.repository.AgentRepository;
import com.agentarena.backend.domain.news.News;
import com.agentarena.backend.domain.news.NewsSentiment;
import com.agentarena.backend.domain.order.Order;
import com.agentarena.backend.domain.order.OrderType;
import com.agentarena.backend.domain.order.dto.OrderResponse;
import com.agentarena.backend.domain.order.exception.OrderNotFoundException;
import com.agentarena.backend.domain.order.repository.OrderRepository;
import com.agentarena.backend.domain.stock.Stock;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private static final Long ORDER_QUANTITY = 10L;
    private static final BigDecimal BUY_IMPACT = new BigDecimal("1.01");
    private static final BigDecimal SELL_IMPACT = new BigDecimal("0.99");

    private final OrderRepository orderRepository;
    private final AgentRepository agentRepository;

    @Override
    @Transactional
    public void simulateForNews(News news) {
        OrderType type = news.getSentiment() == NewsSentiment.POSITIVE ? OrderType.BUY : OrderType.SELL;
        Stock stock = news.getRelatedStock();

        List<Agent> agents = agentRepository.findAll();
        for (Agent agent : agents) {
            BigDecimal executionPrice = stock.getCurrentPrice();

            orderRepository.save(
                    Order.builder()
                            .agent(agent)
                            .stock(stock)
                            .type(type)
                            .quantity(ORDER_QUANTITY)
                            .price(executionPrice)
                            .executedAt(LocalDateTime.now())
                            .build()
            );

            BigDecimal impact = type == OrderType.BUY ? BUY_IMPACT : SELL_IMPACT;
            BigDecimal nextPrice = executionPrice.multiply(impact).setScale(4, RoundingMode.HALF_UP);
            stock.reflectTrade(nextPrice, ORDER_QUANTITY);
        }
    }

    @Override
    public List<OrderResponse> findAll(Long agentId) {
        List<Order> orders = agentId != null
                ? orderRepository.findByAgent_IdOrderByExecutedAtDesc(agentId)
                : orderRepository.findAllByOrderByExecutedAtDesc();
        return orders.stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Override
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);
        return OrderResponse.from(order);
    }
}
