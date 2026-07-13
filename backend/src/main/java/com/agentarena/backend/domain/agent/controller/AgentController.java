package com.agentarena.backend.domain.agent.controller;

import com.agentarena.backend.domain.agent.dto.AgentCreateRequest;
import com.agentarena.backend.domain.agent.dto.AgentResponse;
import com.agentarena.backend.domain.agent.service.AgentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgentResponse create(@Valid @RequestBody AgentCreateRequest request) {
        return agentService.create(request);
    }

    @GetMapping
    public List<AgentResponse> findAll() {
        return agentService.findAll();
    }

    @GetMapping("/{id}")
    public AgentResponse findById(@PathVariable Long id) {
        return agentService.findById(id);
    }
}
