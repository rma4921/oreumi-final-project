package com.estsoft.finalproject.content.model.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record NewsSearchItem(String headline, String summary, String link, LocalDateTime timestamp) { }
