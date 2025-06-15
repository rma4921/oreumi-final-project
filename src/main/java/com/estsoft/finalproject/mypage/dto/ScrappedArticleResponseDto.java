package com.estsoft.finalproject.mypage.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ScrappedArticleResponseDto {

    private Long scrapId;
    private String title;
    private String topic;
    private LocalDateTime scrapDate;
}
