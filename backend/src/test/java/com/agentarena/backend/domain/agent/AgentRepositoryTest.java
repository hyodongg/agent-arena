package com.agentarena.backend.domain.agent;

import com.agentarena.backend.domain.user.User;
import com.agentarena.backend.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class AgentRepositoryTest {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 에이전트를_저장하고_소유자를_조회할_수_있다() {
        User owner = userRepository.save(User.builder()
                .username("owner")
                .tokenBalance(500L)
                .build());

        Agent agent = Agent.builder()
                .owner(owner)
                .name("추세추종봇")
                .investmentPrompt("단기 급등 종목을 추격 매수한다")
                .cumulativeReturn(0.0)
                .build();

        Agent saved = agentRepository.save(agent);

        Optional<Agent> found = agentRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("추세추종봇", found.get().getName());
        assertEquals(owner.getId(), found.get().getOwner().getId());
        assertNotNull(found.get().getCreatedAt());
    }
}
