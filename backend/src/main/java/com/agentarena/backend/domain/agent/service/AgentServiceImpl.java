package com.agentarena.backend.domain.agent.service;

import com.agentarena.backend.domain.agent.Agent;
import com.agentarena.backend.domain.agent.AgentStyle;
import com.agentarena.backend.domain.agent.dto.AgentCreateRequest;
import com.agentarena.backend.domain.agent.dto.AgentResponse;
import com.agentarena.backend.domain.agent.exception.AgentNotFoundException;
import com.agentarena.backend.domain.agent.repository.AgentRepository;
import com.agentarena.backend.domain.user.User;
import com.agentarena.backend.domain.user.exception.UserNotFoundException;
import com.agentarena.backend.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentServiceImpl implements AgentService {

    private static final Double INITIAL_CUMULATIVE_RETURN = 0.0;
    private static final Long INITIAL_CAPITAL = 1_000_000L;

    private final AgentRepository agentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AgentResponse create(AgentCreateRequest request) {
        User owner = userRepository.findById(request.ownerId())
                .orElseThrow(UserNotFoundException::new);

        Agent agent = agentRepository.save(
                Agent.builder()
                        .owner(owner)
                        .name(request.name())
                        .investmentPrompt(request.investmentPrompt())
                        .cumulativeReturn(INITIAL_CUMULATIVE_RETURN)
                        .cashBalance(INITIAL_CAPITAL)
                        .initialCapital(INITIAL_CAPITAL)
                        .style(AgentStyle.classify(request.investmentPrompt()))
                        .build()
        );
        return AgentResponse.from(agent);
    }

    @Override
    public List<AgentResponse> findAll() {
        return agentRepository.findAll().stream()
                .map(AgentResponse::from)
                .toList();
    }

    @Override
    public AgentResponse findById(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(AgentNotFoundException::new);
        return AgentResponse.from(agent);
    }
}
