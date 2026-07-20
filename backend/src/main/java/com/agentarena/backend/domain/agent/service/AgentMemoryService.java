package com.agentarena.backend.domain.agent.service;

import com.agentarena.backend.common.client.openai.OpenAiClient;
import com.agentarena.backend.domain.agent.AgentMemory;
import com.agentarena.backend.domain.agent.repository.AgentMemoryRepository;
import com.agentarena.backend.domain.agent.repository.AgentRepository;
import com.agentarena.backend.domain.order.OrderType;
import com.agentarena.backend.domain.stock.repository.StockRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * 에이전트의 매매 기억을 남기고, 새 뉴스와 의미가 비슷한 과거 기억을 찾아준다.
 *
 * <p>유사도는 자바에서 전체 스캔으로 계산한다. 기억이 수백 건 수준에서는 이게 가장 단순하다.
 * 전용 벡터 저장소는 전체 스캔이 실제로 느려지는 것을 확인한 뒤에 도입한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentMemoryService {

    private static final int RECALL_LIMIT = 3;

    private final AgentMemoryRepository agentMemoryRepository;
    private final AgentRepository agentRepository;
    private final StockRepository stockRepository;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 매매 기억을 남긴다.
     *
     * <p>매매 트랜잭션과 분리한다. 같은 트랜잭션에서 저장이 실패하면 호출한 쪽에서 예외를 잡아도
     * 영속성 컨텍스트가 롤백 전용으로 오염돼 이후 작업이 전부 깨진다. 엔티티를 넘겨받지 않고
     * 식별자로 참조를 다시 잡는 것도 같은 이유다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void remember(Long agentId, Long stockId, String newsTitle, OrderType action, BigDecimal executionPrice) {
        float[] embedding = openAiClient.embed(newsTitle);
        agentMemoryRepository.save(
                AgentMemory.builder()
                        .agent(agentRepository.getReferenceById(agentId))
                        .stock(stockRepository.getReferenceById(stockId))
                        .newsTitle(newsTitle)
                        .action(action)
                        .executionPrice(executionPrice)
                        .embedding(serialize(embedding))
                        .build()
        );
    }

    /** @return 평가가 끝난 기억 중 현재 뉴스와 가장 비슷한 것들. 기억이 없으면 빈 리스트. */
    @Transactional(readOnly = true)
    public List<RecalledMemory> recall(Long agentId, float[] queryEmbedding) {
        return agentMemoryRepository.findByAgent_IdAndReturnRateIsNotNull(agentId).stream()
                .map(memory -> new RecalledMemory(
                        memory.getNewsTitle(),
                        memory.getStock().getName(),
                        memory.getAction(),
                        memory.getReturnRate(),
                        cosineSimilarity(queryEmbedding, deserialize(memory.getEmbedding()))
                ))
                .sorted(Comparator.comparingDouble(RecalledMemory::similarity).reversed())
                .limit(RECALL_LIMIT)
                .toList();
    }

    public float[] embed(String text) {
        return openAiClient.embed(text);
    }

    private String serialize(float[] embedding) {
        return objectMapper.writeValueAsString(embedding);
    }

    private float[] deserialize(String raw) {
        return objectMapper.readValue(raw, float[].class);
    }

    private double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            return 0.0;
        }
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
