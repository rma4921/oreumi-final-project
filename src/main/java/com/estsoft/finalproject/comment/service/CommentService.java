package com.estsoft.finalproject.comment.service;

import com.estsoft.finalproject.Post.domain.ScrapPost;
import com.estsoft.finalproject.Post.repository.ScrapPostRepository;
import com.estsoft.finalproject.comment.repository.CommentRepository;
import com.estsoft.finalproject.user.domain.Users;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import com.estsoft.finalproject.comment.dto.CommentRequest;
import com.estsoft.finalproject.comment.domain.Comment;
import com.estsoft.finalproject.comment.dto.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ScrapPostRepository scrapPostRepository;

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

    public Page<CommentResponseDto> findByUserAndContentContaining(Users user, String keyword,
        Pageable pageable) {
        Page<Comment> commentList = commentRepository.findByUserAndContentContaining(user, keyword,
            pageable);
        return commentList.map(comment -> CommentResponseDto.builder()
            .commentId(comment.getId())
            .scrapId(comment.getScrapPost().getScrappedArticle().getScrapId())
            .postId(comment.getScrapPost().getPostId())
            .articleTitle(comment.getScrapPost().getScrappedArticle().getTitle())
            .content(comment.getContent())
            .createTime(comment.getCreateTime())
            .build());
    }

    public Comment save(CommentRequest request, Users user) {
        validateContent(request.getContent());

        LocalDateTime now = LocalDateTime.now();

        Comment comment = Comment.builder()
            .scrapPost(scrapPostRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."))
            )
            .user(user)
            .content(request.getContent())
            .createTime(now)
            .updateTime(now)
            .build();

        return commentRepository.save(comment);
    }

    public List<Comment> findByPostId(ScrapPost scrapPost) {
        return commentRepository.findByScrapPost(scrapPost);
    }

    @Transactional
    public Comment updateComment(Long commentId, CommentRequest request, Long userId) {
        validateContent(request.getContent());

        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new SecurityException("자신의 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(request.getContent());
        comment.setUpdateTime(LocalDateTime.now());

        return comment;
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new SecurityException("자신의 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }


    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("빈 공백은 등록할 수 없어요");
        }

        if (content.length() > 1000) {
            throw new IllegalArgumentException("댓글은 1000자 이하만 작성 가능합니다.");
        }
    }

    public List<Comment> findByUserId(Long userId) {
        return commentRepository.findByUser_UserId(userId);
    }
}
