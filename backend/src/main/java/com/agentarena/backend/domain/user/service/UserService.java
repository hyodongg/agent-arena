package com.agentarena.backend.domain.user.service;

import com.agentarena.backend.domain.user.dto.UserResponse;

public interface UserService {

    UserResponse signup(String username, String password);

    UserResponse login(String username, String password);

    UserResponse findById(Long id);
}
