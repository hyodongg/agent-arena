package com.agentarena.backend.domain.round.controller;

import com.agentarena.backend.domain.round.dto.RoundResponse;
import com.agentarena.backend.domain.round.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rounds")
@RequiredArgsConstructor
public class RoundController {

    private final RoundService roundService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoundResponse start() {
        return roundService.start();
    }

    @GetMapping("/current")
    public RoundResponse getCurrent() {
        return roundService.getCurrent();
    }
}
