package com.estsoft.finalproject.comment;

import com.estsoft.finalproject.comment.dto.CommentRequest;
import com.estsoft.finalproject.comment.entity.Comment;
import com.estsoft.finalproject.comment.repository.CommentRepository;
import com.estsoft.finalproject.comment.repository.UserRepository;
import com.estsoft.finalproject.comment.service.CommentService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void save_comment_success() {
        // given
        CommentRequest validRequest = CommentRequest.builder()
                .postId(1L)
                .userId(1L)
                .content("comment valid")
                .build();

        User mockUser = User.builder().id(1L).nickname("테스트1").build();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        Comment comment = Comment.builder()
                .id(1L)
                .postId(validRequest.getPostId())
                .user(mockUser)
                .content(validRequest.getContent())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);

        // when
        Comment saved = commentService.save(validRequest);

        // then
        assertNotNull(saved);
        assertEquals("comment valid", saved.getContent());
        assertEquals("테스트1", saved.getUser().getNickname());
    }

    @Test
    void save_comment_blankContent_throwsException() {
        // given
        CommentRequest invalidRequest = CommentRequest.builder()
                .postId(1L)
                .userId(1L)
                .content("   ")
                .build();

        // when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            commentService.save(invalidRequest);
        });

        assertEquals("빈 공백은 등록할 수 없어요", e.getMessage());
    }

    @Test
    void save_comment_tooLongContent_throwsException() {
        // given
        String longContent = "a".repeat(1001);
        CommentRequest request = CommentRequest.builder()
                .postId(1L)
                .userId(1L)
                .content(longContent)
                .build();

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.save(request)
        );

        assertEquals("댓글은 1000자 이하만 작성 가능합니다.", exception.getMessage());
    }

    @Test
    void update_comment_success() {
        // given
        Long commentId = 1L;
        User user = User.builder().id(1L).nickname("user1").build();
        Comment existingComment = Comment.builder()
                .id(commentId)
                .postId(1L)
                .user(user)
                .content("original")
                .createTime(LocalDateTime.now().minusMinutes(5))
                .updateTime(LocalDateTime.now().minusMinutes(5))
                .build();

        CommentRequest updateRequest = CommentRequest.builder()
                .postId(1L)
                .userId(1L)
                .content("updated content")
                .build();

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // when
        Comment updated = commentService.updateComment(commentId, updateRequest);

        // then
        assertEquals("updated content", updated.getContent());
        assertTrue(updated.getUpdateTime().isAfter(updated.getCreateTime()));
    }

    @Test
    void delete_comment_success() {
        // given
        Long commentId = 1L;
        Comment comment = Comment.builder().id(commentId).build();
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when
        commentService.deleteComment(commentId);

        // then
        Mockito.verify(commentRepository).delete(comment);
    }

    @Test
    void find_by_user_id_success() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).nickname("user1").build();
        Comment comment1 = Comment.builder().id(1L).user(user).content("댓글1").build();
        Comment comment2 = Comment.builder().id(2L).user(user).content("댓글2").build();

        Mockito.when(commentRepository.findByUserId(userId)).thenReturn(List.of(comment1, comment2));

        // when
        List<Comment> result = commentService.findByUserId(userId);

        // then
        assertEquals(2, result.size());
        assertEquals("댓글1", result.get(0).getContent());
        assertEquals("댓글2", result.get(1).getContent());
    }
}