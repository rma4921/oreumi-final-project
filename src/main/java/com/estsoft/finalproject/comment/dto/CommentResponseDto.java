package com.estsoft.finalproject.comment.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Long commentId;
    private Long scrapId;
    private String articleTitle;
    private String content;
    private LocalDateTime createTime;
}
