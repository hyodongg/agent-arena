package com.agentarena.backend.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    AGENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 에이전트입니다."),
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 종목입니다."),
    NEWS_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 뉴스입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
