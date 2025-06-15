package com.estsoft.finalproject.content.model.dto;

import java.time.LocalDateTime;

public record NewsBriefingItem(String category, String headline, String content, LocalDateTime timestamp) {
    
}
