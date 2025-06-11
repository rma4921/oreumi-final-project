package com.estsoft.finalproject.comment.service;

import com.estsoft.finalproject.comment.repository.UserRepository;
import com.estsoft.finalproject.comment.entity.User;

import com.estsoft.finalproject.comment.repository.CommentRepository;
import com.estsoft.finalproject.comment.dto.CommentRequest;
import com.estsoft.finalproject.comment.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public Comment save(CommentRequest request) {
        validateContent(request.getContent());

        LocalDateTime now = LocalDateTime.now();

        String nickname = userRepository.findById(request.getUserId())
                .map(User::getNickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 못 찾았습니다."));

        Comment comment = Comment.builder()
                .postId(request.getPostId())
                .userId(request.getUserId())
                .nickname(nickname)
                .content(request.getContent())
                .createTime(now)
                .updateTime(now)
                .build();

        return commentRepository.save(comment);
    }


    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Transactional
    public Comment updateComment(Long commentId, CommentRequest request) {
        validateContent(request.getContent());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        comment.setContent(request.getContent());

        return comment;
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        commentRepository.delete(comment);

        //public void deleteComment(Long commentId, Long userId) {
        //    Comment comment = commentRepository.findById(commentId)
        //        .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
        //
        //    if (!comment.getUserId().equals(userId)) {
        //        throw new SecurityException("자신의 댓글만 삭제할 수 있습니다.");
        //    }
        //
        //    commentRepository.delete(comment);
        //}

        //권한 체크 메서드 준비(삭제/수정)
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
        return commentRepository.findByUserId(userId);
    }


}