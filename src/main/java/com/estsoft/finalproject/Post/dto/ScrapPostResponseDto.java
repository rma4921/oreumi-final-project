package com.estsoft.finalproject.Post.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ScrapPostResponseDto {

    private Long postId;
    private String nickname;
    private String title;
    private String topic;
    private LocalDateTime postDate;
}
