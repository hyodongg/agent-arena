package com.agentarena.backend.domain.round.service;

import com.agentarena.backend.domain.round.dto.RoundResponse;

public interface RoundService {

    RoundResponse start();

    RoundResponse getCurrent();
}
