package com.estsoft.finalproject.content.model.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record NewsDetailItem(String headline, String content, String link, String topic, LocalDateTime timestamp) {
    
}
