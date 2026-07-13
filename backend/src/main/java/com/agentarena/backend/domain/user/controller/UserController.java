package com.agentarena.backend.domain.user.controller;

import com.agentarena.backend.domain.user.dto.UserEnterRequest;
import com.agentarena.backend.domain.user.dto.UserResponse;
import com.agentarena.backend.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/enter")
    public UserResponse enter(@Valid @RequestBody UserEnterRequest request) {
        return userService.enter(request.username());
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }
}
