package com.estsoft.finalproject.comment.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CommentRequest {

    private Long postId;
    private String content;

}