package com.agentarena.backend.domain.user.service;

import com.agentarena.backend.domain.user.User;
import com.agentarena.backend.domain.user.dto.UserResponse;
import com.agentarena.backend.domain.user.exception.UserNotFoundException;
import com.agentarena.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Long INITIAL_TOKEN_BALANCE = 100_000L;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse enter(String username) {
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .username(username)
                                .tokenBalance(INITIAL_TOKEN_BALANCE)
                                .build()
                ));
        return UserResponse.from(user);
    }

    @Override
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return UserResponse.from(user);
    }
}
