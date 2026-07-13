package com.agentarena.backend.domain.agent.repository;

import com.agentarena.backend.domain.agent.AgentHolding;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentHoldingRepository extends JpaRepository<AgentHolding, Long> {

    Optional<AgentHolding> findByAgent_IdAndStock_Id(Long agentId, Long stockId);

    List<AgentHolding> findByAgent_Id(Long agentId);
}
