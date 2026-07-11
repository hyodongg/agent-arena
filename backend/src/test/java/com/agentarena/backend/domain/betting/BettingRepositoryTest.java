package com.agentarena.backend.domain.betting;

import com.agentarena.backend.domain.agent.Agent;
import com.agentarena.backend.domain.agent.AgentRepository;
import com.agentarena.backend.domain.user.User;
import com.agentarena.backend.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class BettingRepositoryTest {

    @Autowired
    private BettingRepository bettingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Test
    void 배팅을_저장하고_조회할_수_있다() {
        User bettor = userRepository.save(User.builder().username("bettor").tokenBalance(2000L).build());
        User agentOwner = userRepository.save(User.builder().username("owner3").tokenBalance(500L).build());
        Agent agent = agentRepository.save(Agent.builder()
                .owner(agentOwner).name("스캘퍼봇").investmentPrompt("초단타 매매").cumulativeReturn(0.0).build());

        Betting betting = Betting.builder()
                .user(bettor)
                .agent(agent)
                .round(1L)
                .amount(100L)
                .status(BettingStatus.IN_PROGRESS)
                .build();

        Betting saved = bettingRepository.save(betting);

        Optional<Betting> found = bettingRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(BettingStatus.IN_PROGRESS, found.get().getStatus());
        assertEquals(bettor.getId(), found.get().getUser().getId());
        assertEquals(agent.getId(), found.get().getAgent().getId());
    }
}
