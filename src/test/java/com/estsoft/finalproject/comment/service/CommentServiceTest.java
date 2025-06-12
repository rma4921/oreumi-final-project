package com.estsoft.finalproject.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.estsoft.finalproject.Post.domain.ScrapPost;
import com.estsoft.finalproject.comment.domain.Comment;
import com.estsoft.finalproject.comment.dto.CommentResponseDto;
import com.estsoft.finalproject.comment.repository.CommentRepository;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.user.domain.Users;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    private Users user;

    private Comment comment;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.updateNickname("테스터");

        ScrappedArticle article = ScrappedArticle.builder()
            .user(user)
            .scrapDate(LocalDateTime.now())
            .title("댓글 테스트 중입니다.")
            .link("https://www.zum.com")
            .description("댓글 테스트")
            .pubDate(LocalDateTime.now())
            .topic("comment test")
            .build();

        ScrapPost scrapPost = new ScrapPost(article);

        comment = Comment.builder()
            .user(user)
            .scrapPost(scrapPost)
            .createTime(LocalDateTime.now())
            .content("댓글이당.")
            .build();
    }

    @Test
    @DisplayName("User가 작성한 댓글 확인 테스트")
    void getCommentsByUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> page = new PageImpl<>(List.of(comment), pageable, 1);

        when(commentRepository.findByUser(user, pageable))
            .thenReturn(page);

        Page<CommentResponseDto> result = commentService.getCommentsByUser(user, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("댓글이당.");

        verify(commentRepository, times(1)).findByUser(user, pageable);
    }

    @Test
    @DisplayName("User가 작성한 댓글 검색 테스트")
    void findByUserAndContentContaining() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> page = new PageImpl<>(List.of(comment), pageable, 1);

        when(commentRepository.findByUserAndContentContaining(user, "댓글", pageable))
            .thenReturn(page);

        Page<CommentResponseDto> result = commentService
            .findByUserAndContentContaining(user, "댓글", pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("댓글이당.");

        verify(commentRepository, times(1))
            .findByUserAndContentContaining(user, "댓글", pageable);
    }
}