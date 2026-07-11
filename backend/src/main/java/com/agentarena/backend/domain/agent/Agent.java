package com.agentarena.backend.domain.agent;

import com.agentarena.backend.common.BaseTimeEntity;
import com.agentarena.backend.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Builder
    public Agent(User owner, String name, String investmentPrompt, Double cumulativeReturn) {
        this.owner = owner;
        this.name = name;
        this.investmentPrompt = investmentPrompt;
        this.cumulativeReturn = cumulativeReturn;
    }
}
