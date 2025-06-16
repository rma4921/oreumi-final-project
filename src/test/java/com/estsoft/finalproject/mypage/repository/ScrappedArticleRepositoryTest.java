//package com.estsoft.finalproject.mypage.repository;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
//import com.estsoft.finalproject.user.domain.Role;
//import com.estsoft.finalproject.user.domain.Users;
//import com.estsoft.finalproject.user.repository.UsersRepository;
//import java.time.LocalDateTime;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.test.context.TestPropertySource;
//
//@DataJpaTest
//class ScrappedArticleRepositoryTest {
//
//    @Autowired
//    private ScrappedArticleRepository scrappedArticleRepository;
//
//    @Autowired
//    private UsersRepository userRepository;
//
//    private Users tester;
//
//    private Users tester2;
//
//    private ScrappedArticle article;
//
//    @BeforeEach
//    public void setUp() {
//        userRepository.deleteAll();
//        scrappedArticleRepository.deleteAll();
//
//        tester = new Users("네이버", "tester@naver.com", "tester", Role.ROLE_USER);
//        tester2 = new Users("구글", "tester@gmail.com", "tester2", Role.ROLE_USER);
//
//        userRepository.save(tester);
//        userRepository.save(tester2);
//
//        for (int i = 1; i <= 2; i++) {
//            scrappedArticleRepository.save(ScrappedArticle.builder()
//                .user(tester)
//                .scrapDate(LocalDateTime.now())
//                .title(i + "번째 테스트 중입니다.")
//                .link("https://www.naver.com")
//                .description(i + "번째 테스트용 더미데이터입니다.")
//                .pubDate(LocalDateTime.now())
//                .topic("test" + i)
//                .build()
//            );
//        }
//        article = scrappedArticleRepository.save(ScrappedArticle.builder()
//            .user(tester2)
//            .scrapDate(LocalDateTime.now())
//            .title("3번째 테스트 중입니다.")
//            .link("https://www.naver.com")
//            .description("3번째 테스트용 더미데이터입니다.")
//            .pubDate(LocalDateTime.now())
//            .topic("test3")
//            .build()
//        );
//    }
//
//    @Test
//    @DisplayName("사용자가 스크랩한 기사 가져오기 테스트")
//    void findAllByUserOrderByScrapDateDesc() {
//        Page<ScrappedArticle> result = scrappedArticleRepository
//            .findAllByUserOrderByScrapDateDesc(tester, PageRequest.of(0, 10));
//
//        assertThat(result.getContent()).hasSize(2);
//        assertThat(result.getContent().get(0).getTopic())
//            .isEqualTo("test2");
//        assertThat(result.getContent().get(1).getTopic())
//            .isEqualTo("test1");
//    }
//
//    @Test
//    @DisplayName("사용자가 스크랩한 기사 중 같은 제목만 가져오기 테스트")
//    void findByUserAndTitleContainingOrderByScrapDateDesc() {
//        Page<ScrappedArticle> result = scrappedArticleRepository
//            .findByUserAndTitleContainingOrderByScrapDateDesc(tester, "1번째",
//                PageRequest.of(0, 10));
//
//        assertThat(result.getContent()).hasSize(1);
//        assertThat(result.getContent().get(0).getTitle()).isEqualTo("1번째 테스트 중입니다.");
//    }
//
//    @Test
//    @DisplayName("ScrapId로 스크랩한 기사 조회 테스트")
//    void findById() {
//        ScrappedArticle result = scrappedArticleRepository.findById(article.getScrapId())
//            .orElseThrow();
//
//        assertThat(result.getUser().getUserId()).isEqualTo(tester2.getUserId());
//        assertThat(result.getTitle()).isEqualTo("3번째 테스트 중입니다.");
//        assertThat(result.getLink()).isEqualTo("https://www.naver.com");
//        assertThat(result.getDescription()).isEqualTo("3번째 테스트용 더미데이터입니다.");
//        assertThat(result.getTopic()).isEqualTo("test3");
//    }
//
//    @Test
//    @DisplayName("스크랩한 기사 삭제 테스트")
//    void deleteById() {
//        scrappedArticleRepository.deleteById(article.getScrapId());
//
//        Optional<ScrappedArticle> result = scrappedArticleRepository.findById(article.getScrapId());
//
//        assertThat(result).isEmpty();
//    }
//}