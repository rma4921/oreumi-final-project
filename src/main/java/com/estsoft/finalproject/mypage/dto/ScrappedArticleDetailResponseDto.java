package com.estsoft.finalproject.mypage.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ScrappedArticleDetailResponseDto {

    private Long scrapId;
    private Long postId;
    private String title;
    private String topic;
    private LocalDateTime scrapDate;
    private String description;
    private String link;
    private LocalDateTime pubDate;
    private boolean isShared;
}
