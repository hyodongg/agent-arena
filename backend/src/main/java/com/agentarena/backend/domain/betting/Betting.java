package com.agentarena.backend.domain.betting;

import com.agentarena.backend.common.BaseTimeEntity;
import com.agentarena.backend.domain.agent.Agent;
import com.agentarena.backend.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bettings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Betting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @Column(nullable = false)
    private Long round;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BettingStatus status;

    @Builder
    public Betting(User user, Agent agent, Long round, Long amount, BettingStatus status) {
        this.user = user;
        this.agent = agent;
        this.round = round;
        this.amount = amount;
        this.status = status;
    }

    public void settle(BettingStatus status) {
        this.status = status;
    }
}
