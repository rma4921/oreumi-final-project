package com.estsoft.finalproject.content.model.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatusCode;

public class AlanResponseDto {

    private final String question;
    private final LocalDateTime timestamp;
    private final String content;
    private final HttpStatusCode responseCode;

    public AlanResponseDto(String question, String content, LocalDateTime timestamp,
        HttpStatusCode responseCode) {
        this.question = question;
        this.timestamp = timestamp;
        this.content = content;
        this.responseCode = responseCode;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getContent() {
        return this.content;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public HttpStatusCode getResponseCode() {
        return this.responseCode;
    }
}
