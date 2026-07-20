package com.agentarena.backend.domain.news.service;

public interface NewsCollectionService {

    /** 종목을 하나씩 돌아가며 실제 뉴스를 수집한다. */
    void collectNext();
}
