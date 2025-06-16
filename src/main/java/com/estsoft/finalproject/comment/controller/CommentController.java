package com.estsoft.finalproject.comment.controller;

import com.estsoft.finalproject.Post.domain.ScrapPost;
import com.estsoft.finalproject.Post.repository.ScrapPostRepository;
import com.estsoft.finalproject.comment.dto.CommentRequest;
import com.estsoft.finalproject.comment.dto.CommentResponse;
import com.estsoft.finalproject.comment.domain.Comment;
import com.estsoft.finalproject.comment.service.CommentService;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import com.estsoft.finalproject.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    private final ScrapPostRepository scrapPostRepository;

    @PostMapping
    public ResponseEntity<?> save(@RequestBody CommentRequest request,
        @AuthenticationPrincipal CustomUsersDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("비회원은 댓글을 달 수 없습니다.");
        }

        Users user = userDetails.getUsers();
        Comment comment = commentService.save(request, user);
        return ResponseEntity.ok(new CommentResponse(comment));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> findByPostId(@PathVariable Long postId) {
        ScrapPost scrapPost = scrapPostRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("postID가 존재하지 않습니다."));

        List<Comment> comments = commentService.findByPostId(scrapPost);
        List<CommentResponse> response = comments.stream()
            .map(CommentResponse::new)
            .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @PathVariable Long commentId,
        @AuthenticationPrincipal CustomUsersDetails userDetails) {

        Long currentUserId = userDetails.getUsers().getUserId();
        commentService.deleteComment(commentId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
        @PathVariable Long commentId,
        @RequestBody CommentRequest request,
        @AuthenticationPrincipal CustomUsersDetails userDetails) {

        Long currentUserId = userDetails.getUsers().getUserId();
        Comment updatedComment = commentService.updateComment(commentId, request,
            currentUserId);

        return ResponseEntity.ok(new CommentResponse(updatedComment));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponse>> findCommentByUserId(@PathVariable Long userId) {
        List<Comment> comments = commentService.findByUserId(userId);
        List<CommentResponse> response = comments.stream()
            .map(CommentResponse::new)
            .toList();
        return ResponseEntity.ok(response);
    }
}