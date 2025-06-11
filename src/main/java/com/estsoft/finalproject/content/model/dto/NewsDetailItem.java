package com.estsoft.finalproject.content.model.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record NewsDetailItem(String headline, String content, String link, String topic, String category, List<String> relatedCompanies) {
    
}
