package com.agentarena.backend.domain.user;

import com.agentarena.backend.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Long tokenBalance;

    @Builder
    public User(String username, String password, Long tokenBalance) {
        this.username = username;
        this.password = password;
        this.tokenBalance = tokenBalance;
    }

    public void deductTokenBalance(Long amount) {
        this.tokenBalance -= amount;
    }

    public void addTokenBalance(Long amount) {
        this.tokenBalance += amount;
    }
}
