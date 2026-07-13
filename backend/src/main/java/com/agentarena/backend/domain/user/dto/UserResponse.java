package com.agentarena.backend.domain.user.dto;

import com.agentarena.backend.domain.user.User;

public record UserResponse(Long id, String username, Long tokenBalance) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getTokenBalance());
    }
}
