package com.agentarena.backend.domain.betting.service;

import com.agentarena.backend.domain.agent.Agent;
import com.agentarena.backend.domain.agent.exception.AgentNotFoundException;
import com.agentarena.backend.domain.agent.repository.AgentRepository;
import com.agentarena.backend.domain.betting.Betting;
import com.agentarena.backend.domain.betting.BettingStatus;
import com.agentarena.backend.domain.betting.dto.BettingCreateRequest;
import com.agentarena.backend.domain.betting.dto.BettingResponse;
import com.agentarena.backend.domain.betting.dto.RoundSettleResponse;
import com.agentarena.backend.domain.betting.exception.BettingNotFoundException;
import com.agentarena.backend.domain.betting.exception.InsufficientTokenBalanceException;
import com.agentarena.backend.domain.betting.repository.BettingRepository;
import com.agentarena.backend.domain.round.Round;
import com.agentarena.backend.domain.round.RoundStatus;
import com.agentarena.backend.domain.round.exception.RoundAlreadySettledException;
import com.agentarena.backend.domain.round.exception.RoundNotFoundException;
import com.agentarena.backend.domain.round.repository.RoundRepository;
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
public class BettingServiceImpl implements BettingService {

    private static final long PAYOUT_MULTIPLIER = 2L;

    private final BettingRepository bettingRepository;
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final RoundRepository roundRepository;

    @Override
    @Transactional
    public BettingResponse create(BettingCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(UserNotFoundException::new);
        Agent agent = agentRepository.findById(request.agentId())
                .orElseThrow(AgentNotFoundException::new);
        Round currentRound = roundRepository.findByStatus(RoundStatus.OPEN)
                .orElseThrow(RoundNotFoundException::new);

        if (user.getTokenBalance() < request.amount()) {
            throw new InsufficientTokenBalanceException();
        }
        user.deductTokenBalance(request.amount());

        Betting betting = bettingRepository.save(
                Betting.builder()
                        .user(user)
                        .agent(agent)
                        .round(currentRound.getId())
                        .amount(request.amount())
                        .status(BettingStatus.IN_PROGRESS)
                        .build()
        );
        return BettingResponse.from(betting);
    }

    @Override
    public List<BettingResponse> findAll(Long userId, Long agentId) {
        List<Betting> bettings;
        if (userId != null) {
            bettings = bettingRepository.findByUser_IdOrderByIdDesc(userId);
        } else if (agentId != null) {
            bettings = bettingRepository.findByAgent_IdOrderByIdDesc(agentId);
        } else {
            bettings = bettingRepository.findAllByOrderByIdDesc();
        }
        return bettings.stream()
                .map(BettingResponse::from)
                .toList();
    }

    @Override
    public BettingResponse findById(Long id) {
        Betting betting = bettingRepository.findById(id)
                .orElseThrow(BettingNotFoundException::new);
        return BettingResponse.from(betting);
    }

    @Override
    @Transactional
    public RoundSettleResponse settleRound(Long round) {
        Round targetRound = roundRepository.findById(round)
                .orElseThrow(RoundNotFoundException::new);
        if (targetRound.getStatus() != RoundStatus.OPEN) {
            throw new RoundAlreadySettledException();
        }

        Agent winner = agentRepository.findFirstByOrderByCumulativeReturnDescIdAsc()
                .orElseThrow(AgentNotFoundException::new);

        List<Betting> inProgressBettings = bettingRepository.findByRoundAndStatus(round, BettingStatus.IN_PROGRESS);

        int wonCount = 0;
        int lostCount = 0;
        for (Betting betting : inProgressBettings) {
            if (betting.getAgent().getId().equals(winner.getId())) {
                betting.settle(BettingStatus.WON);
                betting.getUser().addTokenBalance(betting.getAmount() * PAYOUT_MULTIPLIER);
                wonCount++;
            } else {
                betting.settle(BettingStatus.LOST);
                lostCount++;
            }
        }

        targetRound.settle(winner.getId());

        return new RoundSettleResponse(round, winner.getId(), winner.getName(), wonCount, lostCount);
    }
}
