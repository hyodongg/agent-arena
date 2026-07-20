package com.agentarena.backend.domain.news.repository;

import com.agentarena.backend.domain.news.News;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {

    Optional<News> findFirstByInjectedAtIsNullOrderByPublishedAtAsc();

    List<News> findByInjectedAtIsNotNullOrderByInjectedAtDesc();

    boolean existsBySourceUrl(String sourceUrl);
}
