package com.agentarena.backend.domain.betting.controller;

import com.agentarena.backend.domain.betting.dto.BettingCreateRequest;
import com.agentarena.backend.domain.betting.dto.BettingResponse;
import com.agentarena.backend.domain.betting.service.BettingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bettings")
@RequiredArgsConstructor
public class BettingController {

    private final BettingService bettingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BettingResponse create(@Valid @RequestBody BettingCreateRequest request) {
        return bettingService.create(request);
    }

    @GetMapping
    public List<BettingResponse> findAll(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long agentId
    ) {
        return bettingService.findAll(userId, agentId);
    }

    @GetMapping("/{id}")
    public BettingResponse findById(@PathVariable Long id) {
        return bettingService.findById(id);
    }
}
