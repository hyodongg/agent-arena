package com.agentarena.backend.domain.agent;

import com.agentarena.backend.common.BaseTimeEntity;
import com.agentarena.backend.domain.order.OrderType;
import com.agentarena.backend.domain.stock.Stock;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 에이전트의 매매 하나에 대한 기억.
 *
 * <p>체결 시점에는 성과를 알 수 없으므로 {@code returnRate}는 비어있고,
 * {@code MemoryEvaluationScheduler}가 나중에 채운다. 평가가 끝난 기억만 회상 대상이 된다.
 */
@Entity
@Table(name = "agent_memories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentMemory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private String newsTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType action;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal executionPrice;

    /**
     * 뉴스 제목의 임베딩. JSON 배열 문자열로 저장한다.
     *
     * <p>{@code @Lob}을 쓰면 MySQL에서 tinytext(255바이트)로 매핑돼 256차원 임베딩이 잘린다.
     * 컬럼 타입을 명시한다.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String embedding;

    /** 체결 이후 수익률. 평가 전에는 null이다. */
    @Column
    private Double returnRate;

    @Column
    private LocalDateTime evaluatedAt;

    @Builder
    public AgentMemory(Agent agent, Stock stock, String newsTitle, OrderType action,
                       BigDecimal executionPrice, String embedding) {
        this.agent = agent;
        this.stock = stock;
        this.newsTitle = newsTitle;
        this.action = action;
        this.executionPrice = executionPrice;
        this.embedding = embedding;
    }

    public void evaluate(double returnRate) {
        this.returnRate = returnRate;
        this.evaluatedAt = LocalDateTime.now();
    }
}
