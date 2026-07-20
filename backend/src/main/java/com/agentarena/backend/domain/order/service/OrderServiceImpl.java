package com.agentarena.backend.domain.order.service;

import com.agentarena.backend.domain.agent.Agent;
import com.agentarena.backend.domain.agent.AgentHolding;
import com.agentarena.backend.domain.agent.repository.AgentHoldingRepository;
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

    private static final BigDecimal TRADE_NOTIONAL_AMOUNT = new BigDecimal("100000");

    private final OrderRepository orderRepository;
    private final AgentRepository agentRepository;
    private final AgentHoldingRepository agentHoldingRepository;

    @Override
    @Transactional
    public void simulateForNews(News news) {
        OrderType type = news.getSentiment() == NewsSentiment.POSITIVE ? OrderType.BUY : OrderType.SELL;
        Stock stock = news.getRelatedStock();

        List<Agent> agents = agentRepository.findAll();
        for (Agent agent : agents) {
            if (Math.random() > agent.getStyle().getReactionProbability()) {
                continue;
            }

            BigDecimal executionPrice = stock.getCurrentPrice();
            BigDecimal notionalAmount = TRADE_NOTIONAL_AMOUNT.multiply(BigDecimal.valueOf(agent.getStyle().getNotionalMultiplier()));
            BigDecimal tradeQuantity = notionalAmount.divide(executionPrice, 8, RoundingMode.HALF_UP);

            orderRepository.save(
                    Order.builder()
                            .agent(agent)
                            .stock(stock)
                            .type(type)
                            .quantity(tradeQuantity)
                            .price(executionPrice)
                            .executedAt(LocalDateTime.now())
                            .build()
            );

            settleTrade(agent, stock, type, executionPrice, tradeQuantity);
        }
    }

    private void settleTrade(Agent agent, Stock stock, OrderType type, BigDecimal executionPrice, BigDecimal tradeQuantity) {
        long tradeValue = executionPrice.multiply(tradeQuantity)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
        agent.adjustCash(type == OrderType.BUY ? -tradeValue : tradeValue);

        AgentHolding holding = agentHoldingRepository.findByAgent_IdAndStock_Id(agent.getId(), stock.getId())
                .orElseGet(() -> agentHoldingRepository.save(
                        AgentHolding.builder()
                                .agent(agent)
                                .stock(stock)
                                .quantity(BigDecimal.ZERO)
                                .build()
                ));
        holding.adjustQuantity(type == OrderType.BUY ? tradeQuantity : tradeQuantity.negate());

        agent.updateCumulativeReturn(calculateCumulativeReturn(agent));
    }

    private Double calculateCumulativeReturn(Agent agent) {
        BigDecimal holdingsValue = agentHoldingRepository.findByAgent_Id(agent.getId()).stream()
                .map(holding -> holding.getStock().getCurrentPrice().multiply(holding.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalValue = BigDecimal.valueOf(agent.getCashBalance()).add(holdingsValue);
        BigDecimal initialCapital = BigDecimal.valueOf(agent.getInitialCapital());

        return totalValue.subtract(initialCapital)
                .divide(initialCapital, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
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
