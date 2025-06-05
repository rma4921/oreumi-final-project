package com.estsoft.finalproject.comment.dto;

import com.estsoft.finalproject.comment.entity.Comment;
import com.estsoft.finalproject.comment.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {
    private Long id;
    private Long postId;
    private String nickname;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private boolean isEdited; // 수정됨에 사용할 필드

    public CommentResponse(Comment comment, String nickname) {
        this.id = comment.getId();
        this.postId = comment.getPostId();
        this.nickname = nickname;
        this.content = comment.getContent();
        this.createTime = comment.getCreateTime();
        this.updateTime = comment.getUpdateTime();
        this.isEdited = comment.getUpdateTime().isAfter(comment.getCreateTime());

    }
}