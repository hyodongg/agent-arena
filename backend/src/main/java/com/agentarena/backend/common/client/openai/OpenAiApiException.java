package com.agentarena.backend.common.client.openai;

public class OpenAiApiException extends RuntimeException {

    public OpenAiApiException(String message) {
        super(message);
    }
}
