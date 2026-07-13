package com.agentarena.backend.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void 유저를_저장하고_id로_조회할_수_있다() {
        User user = User.builder()
                .username("tester")
                .tokenBalance(1000L)
                .build();

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("tester", found.get().getUsername());
        assertEquals(1000L, found.get().getTokenBalance());
        assertNotNull(found.get().getCreatedAt());
    }
}
