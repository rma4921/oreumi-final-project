package com.estsoft.finalproject.comment.controller;

import com.estsoft.finalproject.comment.dto.CommentRequest;
import com.estsoft.finalproject.comment.dto.CommentResponse;
import com.estsoft.finalproject.comment.entity.Comment;
import com.estsoft.finalproject.comment.repository.CommentRepository;
import com.estsoft.finalproject.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentRepository commentRepository;

    @PostMapping
    public ResponseEntity<CommentResponse> save(@RequestBody CommentRequest request) {
        Comment comment = commentService.save(request);
        return ResponseEntity.ok(new CommentResponse(comment, comment.getNickname()));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> findByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.findByPostId(postId);
        List<CommentResponse> response = comments.stream()
                .map(comment -> new CommentResponse(comment, comment.getNickname()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();

        //@DeleteMapping("/{commentId}")
        //public ResponseEntity<Void> deleteComment(
        //        @PathVariable Long commentId,
        //        @AuthenticationPrincipal CustomUsersDetails userDetails
        //) {
        //    Long userId = userDetails.getUsers().getUserId();
        //    commentService.deleteComment(commentId, userId);
        //    return ResponseEntity.noContent().build();
        //}

        // 권한 체크 메서드 준비(삭제 /수정)
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest request) {
        Comment comment = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(new CommentResponse(comment, comment.getNickname()));
    }

    @GetMapping("/user/{userId}") // 유저가 작성한 댓글 불러오기
    public ResponseEntity<List<CommentResponse>> findCommentByUserId(@PathVariable Long userId) {
        List<Comment> comments = commentService.findByUserId(userId);
        List<CommentResponse> response = comments.stream()
                .map(comment -> new CommentResponse(comment, comment. getNickname()))
                .toList();
        return ResponseEntity.ok(response);
    }

}