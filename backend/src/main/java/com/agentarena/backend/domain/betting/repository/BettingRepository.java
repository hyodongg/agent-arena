package com.agentarena.backend.domain.betting.repository;

import com.agentarena.backend.domain.betting.Betting;
import com.agentarena.backend.domain.betting.BettingStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BettingRepository extends JpaRepository<Betting, Long> {

    List<Betting> findAllByOrderByIdDesc();

    List<Betting> findByUser_IdOrderByIdDesc(Long userId);

    List<Betting> findByAgent_IdOrderByIdDesc(Long agentId);

    List<Betting> findByRoundAndStatus(Long round, BettingStatus status);
}
