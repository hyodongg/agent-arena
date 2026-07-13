package com.agentarena.backend.domain.betting.service;

import com.agentarena.backend.domain.betting.dto.BettingCreateRequest;
import com.agentarena.backend.domain.betting.dto.BettingResponse;
import com.agentarena.backend.domain.betting.dto.RoundSettleResponse;
import java.util.List;

public interface BettingService {

    BettingResponse create(BettingCreateRequest request);

    List<BettingResponse> findAll(Long userId, Long agentId);

    BettingResponse findById(Long id);

    RoundSettleResponse settleRound(Long round);
}
