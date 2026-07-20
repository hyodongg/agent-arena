package com.agentarena.backend.common.client.naver;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "naver")
public record NaverProperties(String baseUrl, String clientId, String clientSecret) {
}
