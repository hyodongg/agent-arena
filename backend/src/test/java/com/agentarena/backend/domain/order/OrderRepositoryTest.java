package com.agentarena.backend.domain.order;

import com.agentarena.backend.domain.agent.Agent;
import com.agentarena.backend.domain.agent.AgentRepository;
import com.agentarena.backend.domain.stock.Stock;
import com.agentarena.backend.domain.stock.StockRepository;
import com.agentarena.backend.domain.user.User;
import com.agentarena.backend.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Test
    void 주문을_저장하고_조회할_수_있다() {
        User owner = userRepository.save(User.builder().username("owner2").tokenBalance(500L).build());
        Agent agent = agentRepository.save(Agent.builder()
                .owner(owner).name("역추세봇").investmentPrompt("과매도 구간 매수").cumulativeReturn(0.0).build());
        Stock stock = stockRepository.save(Stock.builder()
                .code("BBB").name("가상화학").currentPrice(new BigDecimal("10000.0000")).volume(500L).build());

        Order order = Order.builder()
                .agent(agent)
                .stock(stock)
                .type(OrderType.BUY)
                .quantity(10L)
                .price(new BigDecimal("10050.0000"))
                .executedAt(LocalDateTime.now())
                .build();

        Order saved = orderRepository.save(order);

        Optional<Order> found = orderRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(OrderType.BUY, found.get().getType());
        assertEquals(agent.getId(), found.get().getAgent().getId());
        assertEquals(stock.getId(), found.get().getStock().getId());
    }
}
