package com.agentarena.backend.domain.agent;

import com.agentarena.backend.common.BaseTimeEntity;
import com.agentarena.backend.domain.stock.Stock;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agent_holdings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentHolding extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Builder
    public AgentHolding(Agent agent, Stock stock, BigDecimal quantity) {
        this.agent = agent;
        this.stock = stock;
        this.quantity = quantity;
    }

    public void adjustQuantity(BigDecimal delta) {
        this.quantity = this.quantity.add(delta);
    }
}
