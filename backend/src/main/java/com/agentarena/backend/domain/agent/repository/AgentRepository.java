package com.agentarena.backend.domain.agent.repository;

import com.agentarena.backend.domain.agent.Agent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findFirstByOrderByCumulativeReturnDescIdAsc();
}
