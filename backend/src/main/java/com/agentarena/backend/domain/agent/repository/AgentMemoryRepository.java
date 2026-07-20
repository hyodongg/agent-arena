package com.agentarena.backend.domain.agent.repository;

import com.agentarena.backend.domain.agent.AgentMemory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentMemoryRepository extends JpaRepository<AgentMemory, Long> {

    List<AgentMemory> findByAgent_IdAndReturnRateIsNotNull(Long agentId);

    List<AgentMemory> findByReturnRateIsNullAndCreatedAtBefore(LocalDateTime threshold);
}
