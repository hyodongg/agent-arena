package com.agentarena.backend.domain.news;

import com.agentarena.backend.common.BaseTimeEntity;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock relatedStock;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NewsSentiment sentiment;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @Column
    private LocalDateTime injectedAt;

    @Builder
    public News(Stock relatedStock, String title, NewsSentiment sentiment, LocalDateTime publishedAt) {
        this.relatedStock = relatedStock;
        this.title = title;
        this.sentiment = sentiment;
        this.publishedAt = publishedAt;
    }

    public void inject(LocalDateTime injectedAt) {
        this.injectedAt = injectedAt;
    }
}
