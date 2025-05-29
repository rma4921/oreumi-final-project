package com.estsoft.finalproject.mypage.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScrappedArticleDetailResponseDto {

    private Long scrapId;
    private String title;
    private String topic;
    private LocalDateTime scrapDate;
    private String description;
    private String link;
    private LocalDateTime pubDate;
    private boolean isShared;
}
