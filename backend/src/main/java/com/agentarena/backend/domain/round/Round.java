package com.agentarena.backend.domain.round;

import com.agentarena.backend.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rounds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Round extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoundStatus status;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime settledAt;

    @Column
    private Long winningAgentId;

    @Builder
    public Round(RoundStatus status, LocalDateTime startedAt) {
        this.status = status;
        this.startedAt = startedAt;
    }

    public void settle(Long winningAgentId) {
        this.status = RoundStatus.SETTLED;
        this.settledAt = LocalDateTime.now();
        this.winningAgentId = winningAgentId;
    }
}
