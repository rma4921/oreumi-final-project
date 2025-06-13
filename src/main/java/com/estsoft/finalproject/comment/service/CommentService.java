package com.estsoft.finalproject.comment.service;

import com.estsoft.finalproject.comment.domain.Comment;
import com.estsoft.finalproject.comment.dto.CommentResponseDto;
import com.estsoft.finalproject.comment.repository.CommentRepository;
import com.estsoft.finalproject.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Page<CommentResponseDto> getCommentsByUser(Users user, Pageable pageable) {
        Page<Comment> commentList = commentRepository.findByUser(user, pageable);
        return commentList.map(comment -> CommentResponseDto.builder()
            .commentId(comment.getId())
            .scrapId(comment.getScrapPost().getScrappedArticle().getScrapId())
            .postId(comment.getScrapPost().getPostId())
            .articleTitle(comment.getScrapPost().getScrappedArticle().getTitle())
            .content(comment.getContent())
            .createTime(comment.getCreateTime())
            .build());
    }

    public Page<CommentResponseDto> findByUserAndContentContaining(Users user, String keyword, Pageable pageable) {
        Page<Comment> commentList = commentRepository.findByUserAndContentContaining(user, keyword, pageable);
        return commentList.map(comment -> CommentResponseDto.builder()
            .commentId(comment.getId())
            .scrapId(comment.getScrapPost().getScrappedArticle().getScrapId())
            .postId(comment.getScrapPost().getPostId())
            .articleTitle(comment.getScrapPost().getScrappedArticle().getTitle())
            .content(comment.getContent())
            .createTime(comment.getCreateTime())
            .build());
    }

}
