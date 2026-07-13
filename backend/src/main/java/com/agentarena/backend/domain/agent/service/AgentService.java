package com.agentarena.backend.domain.agent.service;

import com.agentarena.backend.domain.agent.dto.AgentCreateRequest;
import com.agentarena.backend.domain.agent.dto.AgentResponse;
import java.util.List;

public interface AgentService {

    AgentResponse create(AgentCreateRequest request);

    List<AgentResponse> findAll();

    AgentResponse findById(Long id);
}
