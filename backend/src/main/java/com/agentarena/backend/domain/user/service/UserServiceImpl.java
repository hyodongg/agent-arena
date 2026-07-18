package com.agentarena.backend.domain.user.service;

import com.agentarena.backend.domain.user.User;
import com.agentarena.backend.domain.user.dto.UserResponse;
import com.agentarena.backend.domain.user.exception.DuplicateUsernameException;
import com.agentarena.backend.domain.user.exception.InvalidCredentialsException;
import com.agentarena.backend.domain.user.exception.PasswordTooLongException;
import com.agentarena.backend.domain.user.exception.UserNotFoundException;
import com.agentarena.backend.domain.user.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Long INITIAL_TOKEN_BALANCE = 100_000L;
    private static final int BCRYPT_MAX_PASSWORD_BYTES = 72;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse signup(String username, String password) {
        validatePasswordByteLength(password);

        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateUsernameException();
        }

        User user = userRepository.save(
                User.builder()
                        .username(username)
                        .password(passwordEncoder.encode(password))
                        .tokenBalance(INITIAL_TOKEN_BALANCE)
                        .build()
        );
        return UserResponse.from(user);
    }

    @Override
    public UserResponse login(String username, String password) {
        validatePasswordByteLength(password);

        User user = userRepository.findByUsername(username)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return UserResponse.from(user);
    }

    private void validatePasswordByteLength(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_PASSWORD_BYTES) {
            throw new PasswordTooLongException();
        }
    }

    @Override
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return UserResponse.from(user);
    }
}
