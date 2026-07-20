package com.agentarena.backend.domain.order.service;

import com.agentarena.backend.domain.agent.Agent;
import com.agentarena.backend.domain.agent.AgentHolding;
import com.agentarena.backend.domain.agent.repository.AgentHoldingRepository;
import com.agentarena.backend.domain.agent.repository.AgentRepository;
import com.agentarena.backend.domain.agent.service.AgentAction;
import com.agentarena.backend.domain.agent.service.AgentDecision;
import com.agentarena.backend.domain.agent.service.AgentDecisionMaker;
import com.agentarena.backend.domain.agent.event.TradeExecutedEvent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal TRADE_NOTIONAL_AMOUNT = new BigDecimal("100000");

    private final OrderRepository orderRepository;
    private final AgentRepository agentRepository;
    private final AgentHoldingRepository agentHoldingRepository;
    private final AgentDecisionMaker agentDecisionMaker;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void simulateForNews(News news) {
        Stock stock = news.getRelatedStock();
        List<Agent> agents = agentRepository.findAll();
        if (agents.isEmpty()) {
            return;
        }

        Map<Long, BigDecimal> holdingQuantities = loadHoldingQuantities(agents, stock);

        try {
            applyDecisions(news, stock, agents, holdingQuantities);
        } catch (Exception e) {
            // LLM 장애로 아레나 전체가 멈추면 안 된다. 기존 확률 규칙으로 대신 움직인다.
            log.warn("LLM 의사결정 실패, AgentStyle 규칙으로 폴백한다: {}", e.getMessage());
            applyStyleFallback(news, stock, agents);
        }
    }

    private void applyDecisions(News news, Stock stock, List<Agent> agents, Map<Long, BigDecimal> holdingQuantities) {
        List<AgentDecision> decisions = agentDecisionMaker.decide(news, agents, holdingQuantities);
        Map<Long, Agent> agentsById = agents.stream()
                .collect(Collectors.toMap(Agent::getId, Function.identity()));

        for (AgentDecision decision : decisions) {
            Agent agent = agentsById.get(decision.agentId());
            if (agent == null || !decision.isTrade()) {
                continue;
            }

            OrderType type = decision.action() == AgentAction.BUY ? OrderType.BUY : OrderType.SELL;
            BigDecimal holding = holdingQuantities.getOrDefault(agent.getId(), BigDecimal.ZERO);
            if (type == OrderType.SELL && holding.signum() <= 0) {
                // 보유하지 않은 종목은 팔 수 없다. 공매도는 이 시뮬레이션 범위가 아니다.
                continue;
            }

            BigDecimal notionalAmount = TRADE_NOTIONAL_AMOUNT.multiply(BigDecimal.valueOf(decision.conviction()));
            executeTrade(agent, stock, type, notionalAmount);
            rememberTrade(agent, stock, news.getTitle(), type);
        }
    }

    /**
     * 기억 저장은 이벤트로 미룬다. 이 트랜잭션이 커밋된 뒤 {@code TradeMemoryListener}가 처리한다.
     *
     * <p>여기서 바로 저장하면 임베딩 API 장애가 체결까지 되돌리고, 별도 트랜잭션으로 열면
     * 이 트랜잭션이 잡고 있는 행 잠금을 스스로 기다리다 타임아웃 난다.
     */
    private void rememberTrade(Agent agent, Stock stock, String newsTitle, OrderType type) {
        eventPublisher.publishEvent(new TradeExecutedEvent(
                agent.getId(), agent.getName(), stock.getId(), newsTitle, type, stock.getCurrentPrice()
        ));
    }

    private void applyStyleFallback(News news, Stock stock, List<Agent> agents) {
        OrderType type = news.getSentiment() == NewsSentiment.POSITIVE ? OrderType.BUY : OrderType.SELL;
        for (Agent agent : agents) {
            if (Math.random() > agent.getStyle().getReactionProbability()) {
                continue;
            }
            BigDecimal notionalAmount = TRADE_NOTIONAL_AMOUNT
                    .multiply(BigDecimal.valueOf(agent.getStyle().getNotionalMultiplier()));
            executeTrade(agent, stock, type, notionalAmount);
        }
    }

    private void executeTrade(Agent agent, Stock stock, OrderType type, BigDecimal notionalAmount) {
        BigDecimal executionPrice = stock.getCurrentPrice();
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

    private Map<Long, BigDecimal> loadHoldingQuantities(List<Agent> agents, Stock stock) {
        Map<Long, BigDecimal> quantities = new HashMap<>();
        for (Agent agent : agents) {
            agentHoldingRepository.findByAgent_IdAndStock_Id(agent.getId(), stock.getId())
                    .ifPresent(holding -> quantities.put(agent.getId(), holding.getQuantity()));
        }
        return quantities;
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
