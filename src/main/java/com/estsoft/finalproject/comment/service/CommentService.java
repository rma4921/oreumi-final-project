package com.estsoft.finalproject.comment.service;

import com.estsoft.finalproject.comment.domain.Comment;
import com.estsoft.finalproject.comment.dto.CommentResponseDto;
import com.estsoft.finalproject.comment.repository.CommentRepository;
import com.estsoft.finalproject.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Page<CommentResponseDto> getCommentsByUser(User user, Pageable pageable) {
        Page<Comment> commentList = commentRepository.findByUser(user, pageable);
        return commentList.map(comment -> CommentResponseDto.builder()
            .commentId(comment.getId())
            .scrapId(comment.getScrapPost().getScrappedArticle().getScrapId())
            .articleTitle(comment.getScrapPost().getScrappedArticle().getTitle())
            .content(comment.getContent())
            .createTime(comment.getCreateTime())
            .build());
    }

    public Page<CommentResponseDto> findByUserAndContentContaining(User user, String keyword, Pageable pageable) {
        Page<Comment> commentList = commentRepository.findByUserAndContentContaining(user, keyword, pageable);
        return commentList.map(comment -> CommentResponseDto.builder()
            .commentId(comment.getId())
            .scrapId(comment.getScrapPost().getScrappedArticle().getScrapId())
            .articleTitle(comment.getScrapPost().getScrappedArticle().getTitle())
            .content(comment.getContent())
            .createTime(comment.getCreateTime())
            .build());
    }

}
