package com.agentarena.backend.domain.agent.repository;

import com.agentarena.backend.domain.agent.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, Long> {
}
