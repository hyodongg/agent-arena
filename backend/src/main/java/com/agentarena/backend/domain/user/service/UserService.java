package com.agentarena.backend.domain.user.service;

import com.agentarena.backend.domain.user.dto.UserResponse;

public interface UserService {

    UserResponse enter(String username);

    UserResponse findById(Long id);
}
