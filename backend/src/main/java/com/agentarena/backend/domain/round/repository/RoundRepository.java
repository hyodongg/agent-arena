package com.agentarena.backend.domain.round.repository;

import com.agentarena.backend.domain.round.Round;
import com.agentarena.backend.domain.round.RoundStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepository extends JpaRepository<Round, Long> {

    Optional<Round> findByStatus(RoundStatus status);
}
