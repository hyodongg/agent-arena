package com.agentarena.backend.domain.stock;

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

import java.math.BigDecimal;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentPrice;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal volume;

    @Builder
    public Stock(String code, String name, BigDecimal currentPrice, BigDecimal volume) {
        this.code = code;
        this.name = name;
        this.currentPrice = currentPrice;
        this.volume = volume;
    }

    /**
     * 외부 시세 API로 받아온 시장 데이터를 반영한다.
     *
     * <p>가격과 거래량을 바꾸는 유일한 경로다. 에이전트의 가상 체결은 이 값을 읽기만 한다.
     */
    public void syncMarketData(BigDecimal currentPrice, BigDecimal volume) {
        this.currentPrice = currentPrice;
        this.volume = volume;
    }
}
