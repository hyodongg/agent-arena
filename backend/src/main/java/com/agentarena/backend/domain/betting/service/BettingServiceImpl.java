package com.agentarena.backend.domain.betting.service;

import com.agentarena.backend.domain.agent.Agent;
import com.agentarena.backend.domain.agent.exception.AgentNotFoundException;
import com.agentarena.backend.domain.agent.repository.AgentRepository;
import com.agentarena.backend.domain.betting.Betting;
import com.agentarena.backend.domain.betting.BettingStatus;
import com.agentarena.backend.domain.betting.dto.BettingCreateRequest;
import com.agentarena.backend.domain.betting.dto.BettingResponse;
import com.agentarena.backend.domain.betting.exception.BettingNotFoundException;
import com.agentarena.backend.domain.betting.exception.InsufficientTokenBalanceException;
import com.agentarena.backend.domain.betting.repository.BettingRepository;
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

    private final BettingRepository bettingRepository;
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;

    @Override
    @Transactional
    public BettingResponse create(BettingCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(UserNotFoundException::new);
        Agent agent = agentRepository.findById(request.agentId())
                .orElseThrow(AgentNotFoundException::new);

        if (user.getTokenBalance() < request.amount()) {
            throw new InsufficientTokenBalanceException();
        }
        user.deductTokenBalance(request.amount());

        Betting betting = bettingRepository.save(
                Betting.builder()
                        .user(user)
                        .agent(agent)
                        .round(request.round())
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
}
