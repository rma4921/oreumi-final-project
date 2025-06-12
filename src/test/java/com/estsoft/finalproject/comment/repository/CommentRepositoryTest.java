package com.estsoft.finalproject.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.estsoft.finalproject.Post.domain.ScrapPost;
import com.estsoft.finalproject.Post.repository.ScrapPostRepository;
import com.estsoft.finalproject.comment.domain.Comment;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.mypage.repository.ScrappedArticleRepository;
import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.repository.UsersRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private ScrapPostRepository scrapPostRepository;

    @Autowired
    private ScrappedArticleRepository scrappedArticleRepository;

    private Users user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        scrapPostRepository.deleteAll();
        scrappedArticleRepository.deleteAll();
        commentRepository.deleteAll();

        user = new Users();
        user.updateNickname("테스터");
        userRepository.save(user);

        ScrappedArticle article = ScrappedArticle.builder()
            .user(user)
            .scrapDate(LocalDateTime.now())
            .title("댓글 테스트 중입니다.")
            .link("https://www.zum.com")
            .description("댓글 테스트")
            .pubDate(LocalDateTime.now())
            .topic("comment test")
            .build();
        scrappedArticleRepository.save(article);

        ScrapPost scrapPost = new ScrapPost(article);
        scrapPostRepository.save(scrapPost);

        Comment comment = Comment.builder()
            .user(user)
            .scrapPost(scrapPost)
            .createTime(LocalDateTime.now())
            .content("댓글이당.")
            .build();
        Comment comment2 = Comment.builder()
            .user(user)
            .scrapPost(scrapPost)
            .createTime(LocalDateTime.now())
            .content("두 번째 댓글이당.")
            .build();
        commentRepository.save(comment);
        commentRepository.save(comment2);
    }

    @Test
    @DisplayName("User가 작성한 댓글 확인 테스트")
    void findByUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> page = commentRepository.findByUser(user, pageable);

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent().get(0).getContent()).isEqualTo("댓글이당.");
        assertThat(page.getContent().get(0).getUser().getNickname()).isEqualTo("테스터");
        assertThat(page.getContent().get(1).getContent()).isEqualTo("두 번째 댓글이당.");
        assertThat(page.getContent().get(1).getUser().getNickname()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("User가 작성한 댓글 검색 테스트")
    void findByUserAndContentContaining() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> page = commentRepository
            .findByUserAndContentContaining(user, "두 번째", pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getContent()).isEqualTo("두 번째 댓글이당.");
        assertThat(page.getContent().get(0).getUser().getNickname()).isEqualTo("테스터");
    }
}