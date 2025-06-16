package com.estsoft.finalproject.comment;

import com.estsoft.finalproject.Post.domain.ScrapPost;
import com.estsoft.finalproject.comment.dto.CommentRequest;
import com.estsoft.finalproject.comment.domain.Comment;
import com.estsoft.finalproject.comment.repository.CommentRepository;
import com.estsoft.finalproject.comment.service.CommentService;
import com.estsoft.finalproject.user.domain.Role;
import com.estsoft.finalproject.user.domain.Users;
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
    private com.estsoft.finalproject.Post.repository.ScrapPostRepository scrapPostRepository;

    @Test
    void save_comment_success() {
        // given
        CommentRequest validRequest = CommentRequest.builder()
                .postId(1L)
                .content("comment valid")
                .build();

        Users mockUser = new Users("google", "asd123", "산적", Role.ROLE_USER);
        mockUser.updateId(1L);

        ScrapPost mockPost = ScrapPost.builder().postId(1L).build();

        Mockito.when(scrapPostRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        Comment comment = Comment.builder()
                .id(1L)
                .scrapPost(mockPost)
                .user(mockUser)
                .content(validRequest.getContent())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);

        // when
        Comment saved = commentService.save(validRequest, mockUser);

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
                .content("   ")
                .build();

        Users mockUser = new Users("google", "asd12", "산적", Role.ROLE_USER);
        mockUser.updateId(1L);

        // when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            commentService.save(invalidRequest, mockUser);
        });

        assertEquals("빈 공백은 등록할 수 없어요", e.getMessage());
    }

    @Test
    void save_comment_tooLongContent_throwsException() {
        // given
        String longContent = "a".repeat(1001);
        CommentRequest request = CommentRequest.builder()
                .postId(1L)
                .content(longContent)
                .build();

        Users mockUser = new Users("google", "ab2sa", "산적", Role.ROLE_USER);
        mockUser.updateId(1L);

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.save(request, mockUser)
        );

        assertEquals("댓글은 1000자 이하만 작성 가능합니다.", exception.getMessage());
    }

    @Test
    void update_comment_success() {
        // given
        Long commentId = 1L;
        Users user = new Users("google", "szx", "산적", Role.ROLE_USER);
        user.updateId(1L);

        ScrapPost post = ScrapPost.builder().postId(1L).build();

        Comment existingComment = Comment.builder()
                .id(commentId)
                .scrapPost(post)
                .user(user)
                .content("수정전")
                .createTime(LocalDateTime.now().minusMinutes(5))
                .updateTime(LocalDateTime.now().minusMinutes(5))
                .build();

        CommentRequest updateRequest = CommentRequest.builder()
                .postId(1L)
                .content("수정후")
                .build();

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // when
        Comment updated = commentService.updateComment(commentId, updateRequest, user.getUserId());

        // then
        assertEquals("updated content", updated.getContent());
        assertTrue(updated.getUpdateTime().isAfter(updated.getCreateTime()));
    }

    @Test
    void delete_comment_success() {
        // given
        Long commentId = 1L;
        Users user = new Users("google", "12ddd", "dd", Role.ROLE_USER);
        user.updateId(1L);

        ScrapPost post = ScrapPost.builder().postId(1L).build();

        Comment comment = Comment.builder().id(commentId).user(user).scrapPost(post).build();
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when
        commentService.deleteComment(commentId, user.getUserId());

        // then
        Mockito.verify(commentRepository).delete(comment);
    }

    @Test
    void find_by_user_id_success() {
        // given
        Long userId = 1L;
        Users user = new Users("google", "asd123", "산적", Role.ROLE_USER);
        user.updateId(userId);

        Comment comment1 = Comment.builder().id(1L).user(user).content("댓글1").build();
        Comment comment2 = Comment.builder().id(2L).user(user).content("댓글2").build();

        Mockito.when(commentRepository.findByUser_UserId(userId)).thenReturn(List.of(comment1, comment2));

        // when
        List<Comment> result = commentService.findByUserId(userId);

        // then
        assertEquals(2, result.size());
        assertEquals("댓글1", result.get(0).getContent());
        assertEquals("댓글2", result.get(1).getContent());
    }

    @Test
    void delete_comment_notAuthor_throwsException() {
        // given
        Long commentId = 1L;

        Users author = new Users("google", "Auser", "산적", Role.ROLE_USER);
        author.updateId(1L);

        Users attacker = new Users("naver", "Buser", "김준형", Role.ROLE_USER);
        attacker.updateId(2L);

        ScrapPost post = ScrapPost.builder().postId(1L).build();

        Comment comment = Comment.builder().id(commentId).user(author).scrapPost(post).build();

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when + then
        SecurityException exception = assertThrows(SecurityException.class, () ->
                commentService.deleteComment(commentId, attacker.getUserId())
        );

        assertEquals("자신의 댓글만 삭제할 수 있습니다.", exception.getMessage());
    }
}