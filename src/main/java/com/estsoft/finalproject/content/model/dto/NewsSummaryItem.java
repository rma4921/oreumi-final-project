package com.estsoft.finalproject.content.model.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record NewsSummaryItem(String headline, String content, String link, String category,
                              LocalDateTime timestamp) {

}
