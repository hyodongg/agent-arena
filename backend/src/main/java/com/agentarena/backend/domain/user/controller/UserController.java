package com.agentarena.backend.domain.user.controller;

import com.agentarena.backend.domain.user.dto.UserLoginRequest;
import com.agentarena.backend.domain.user.dto.UserResponse;
import com.agentarena.backend.domain.user.dto.UserSignupRequest;
import com.agentarena.backend.domain.user.service.UserService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(@Valid @RequestBody UserSignupRequest request) {
        return userService.signup(request.username(), request.password());
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody UserLoginRequest request) {
        return userService.login(request.username(), request.password());
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }
}
