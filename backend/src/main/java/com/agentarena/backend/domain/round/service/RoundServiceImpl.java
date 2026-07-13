package com.agentarena.backend.domain.round.service;

import com.agentarena.backend.domain.round.Round;
import com.agentarena.backend.domain.round.RoundStatus;
import com.agentarena.backend.domain.round.dto.RoundResponse;
import com.agentarena.backend.domain.round.exception.RoundAlreadyOpenException;
import com.agentarena.backend.domain.round.exception.RoundNotFoundException;
import com.agentarena.backend.domain.round.repository.RoundRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoundServiceImpl implements RoundService {

    private final RoundRepository roundRepository;

    @Override
    @Transactional
    public RoundResponse start() {
        if (roundRepository.findByStatus(RoundStatus.OPEN).isPresent()) {
            throw new RoundAlreadyOpenException();
        }

        Round round = roundRepository.save(
                Round.builder()
                        .status(RoundStatus.OPEN)
                        .startedAt(LocalDateTime.now())
                        .build()
        );
        return RoundResponse.from(round);
    }

    @Override
    public RoundResponse getCurrent() {
        Round round = roundRepository.findByStatus(RoundStatus.OPEN)
                .orElseThrow(RoundNotFoundException::new);
        return RoundResponse.from(round);
    }
}
