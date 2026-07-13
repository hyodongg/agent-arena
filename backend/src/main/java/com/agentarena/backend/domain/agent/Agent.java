package com.agentarena.backend.domain.agent;

import com.agentarena.backend.common.BaseTimeEntity;
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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(nullable = false)
    private String investmentPrompt;

    @Column(nullable = false)
    private Double cumulativeReturn;

    @Column(nullable = false)
    private Long cashBalance;

    @Column(nullable = false)
    private Long initialCapital;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentStyle style;

    @Builder
    public Agent(User owner, String name, String investmentPrompt, Double cumulativeReturn,
                 Long cashBalance, Long initialCapital, AgentStyle style) {
        this.owner = owner;
        this.name = name;
        this.investmentPrompt = investmentPrompt;
        this.cumulativeReturn = cumulativeReturn;
        this.cashBalance = cashBalance;
        this.initialCapital = initialCapital;
        this.style = style;
    }

    public void adjustCash(Long delta) {
        this.cashBalance += delta;
    }

    public void updateCumulativeReturn(Double value) {
        this.cumulativeReturn = value;
    }
}
