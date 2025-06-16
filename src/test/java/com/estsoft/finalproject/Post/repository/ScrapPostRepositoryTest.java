//package com.estsoft.finalproject.Post.repository;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import com.estsoft.finalproject.Post.domain.ScrapPost;
//import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
//import com.estsoft.finalproject.mypage.repository.ScrappedArticleRepository;
//import com.estsoft.finalproject.user.domain.Role;
//import com.estsoft.finalproject.user.domain.Users;
//import com.estsoft.finalproject.user.repository.UsersRepository;
//
//import java.time.LocalDateTime;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.ActiveProfiles;
//
//@DataJpaTest
//@ActiveProfiles("test")
//class ScrapPostRepositoryTest {
//
//    @Autowired
//    private ScrappedArticleRepository scrappedArticleRepository;
//
//    @Autowired
//    private UsersRepository userRepository;
//
//    @Autowired
//    private ScrapPostRepository scrapPostRepository;
//
//    private ScrappedArticle article;
//
//    @BeforeEach
//    void setUp() {
//        Users tester = new Users("네이버", "tester@naver.com", "tester", Role.ROLE_USER);
//        userRepository.save(tester);
//
//        article = ScrappedArticle.builder()
//            .user(tester)
//            .scrapDate(LocalDateTime.now())
//            .title("테스트 중입니다.")
//            .link("https://www.google.com")
//            .description("테스트용 더미데이터입니다.")
//            .pubDate(LocalDateTime.now())
//            .topic("test")
//            .build();
//        scrappedArticleRepository.save(article);
//
//        ScrapPost scrapPost = ScrapPost.builder()
//            .scrappedArticle(article)
//            .postDate(LocalDateTime.now())
//            .build();
//        scrapPostRepository.save(scrapPost);
//
//    }
//
//    @Test
//    @DisplayName("스크랩한 존재 유무 테스트")
//    void existsByScrappedArticle_ScrapId() {
//        boolean isExist = scrapPostRepository.existsByScrappedArticle_ScrapId(article.getScrapId());
//        boolean isExist2 = scrapPostRepository.existsByScrappedArticle_ScrapId(1234L);
//
//        assertTrue(isExist);
//        assertFalse(isExist2);
//    }
//
//    @Test
//    @DisplayName("스크랩한 기사로 공유한 기사 확인 테스트")
//    void findByScrappedArticle() {
//        ScrapPost result = scrapPostRepository.findByScrappedArticle(article);
//
//        assertThat(result).isNotNull();
//        assertThat(result.getScrappedArticle().getScrapId()).isEqualTo(article.getScrapId());
//        assertThat(result.getScrappedArticle().getTitle()).isEqualTo("테스트 중입니다.");
//    }
//
//    @Test
//    @DisplayName("스크랩한 기사로 공유한 기사 검색 테스트")
//    void findByScrappedArticle_TitleContaining() {
//        Pageable pageable = PageRequest.of(0, 10);
//
//        Page<ScrapPost> result = scrapPostRepository
//            .findByScrappedArticle_TitleContaining("테스트 중입니다.", pageable);
//        Page<ScrapPost> empty = scrapPostRepository
//            .findByScrappedArticle_TitleContaining("ㅋㅋ", pageable);
//
//        assertThat(result.getTotalElements()).isEqualTo(1);
//        assertThat(result.getContent().get(0)
//            .getScrappedArticle().getTitle()).isEqualTo("테스트 중입니다.");
//
//        assertThat(empty.getTotalElements()).isEqualTo(0);
//    }
//}