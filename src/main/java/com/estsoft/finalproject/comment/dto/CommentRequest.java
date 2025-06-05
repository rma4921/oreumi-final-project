package com.estsoft.finalproject.comment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CommentRequest {
    private Long postId;
    private Long userId; // 로그인 기능 구현시 제거
    private String content;

}