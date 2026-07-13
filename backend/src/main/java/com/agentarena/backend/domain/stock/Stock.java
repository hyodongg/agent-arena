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

    @Column(nullable = false)
    private Long volume;

    @Builder
    public Stock(String code, String name, BigDecimal currentPrice, Long volume) {
        this.code = code;
        this.name = name;
        this.currentPrice = currentPrice;
        this.volume = volume;
    }

    public void reflectTrade(BigDecimal executedPrice, Long tradedQuantity) {
        this.currentPrice = executedPrice;
        this.volume += tradedQuantity;
    }
}
