//package com.estsoft.finalproject.mypage.repository;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.estsoft.finalproject.mypage.domain.RelatedStock;
//import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
//import com.estsoft.finalproject.user.domain.Role;
//import com.estsoft.finalproject.user.domain.Users;
//import com.estsoft.finalproject.user.repository.UsersRepository;
//import java.time.LocalDateTime;
//import java.util.List;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.TestPropertySource;
//
//@DataJpaTest
//@TestPropertySource(locations = "classpath:application-test.properties")
//class RelatedStockRepositoryTest {
//
//    @Autowired
//    private RelatedStockRepository relatedStockRepository;
//
//    @Autowired
//    private ScrappedArticleRepository scrappedArticleRepository;
//
//    @Autowired
//    private UsersRepository userRepository;
//
//    @BeforeEach
//    public void setUp() {
//        relatedStockRepository.deleteAll();
//        scrappedArticleRepository.deleteAll();
//    }
//
//    @Test
//    @DisplayName("scrapId의 관련 주식 가져오기 테스트")
//    void findByScrappedArticle_ScrapId() {
//        Users tester = new Users("네이버", "tester@naver.com", "tester", Role.ROLE_USER);
//
//        userRepository.save(tester);
//
//        ScrappedArticle article = ScrappedArticle.builder()
//            .user(tester)
//            .scrapDate(LocalDateTime.now())
//            .title("테스트 중입니다.")
//            .link("https://www.google.com")
//            .description("테스트용 더미데이터입니다.")
//            .pubDate(LocalDateTime.now())
//            .topic("test")
//            .build();
//
//        scrappedArticleRepository.save(article);
//
//        RelatedStock stock1 = RelatedStock.builder()
//            .scrappedArticle(article)
//            .name("1번")
//            .build();
//
//        RelatedStock stock2 = RelatedStock.builder()
//            .scrappedArticle(article)
//            .name("2번")
//            .build();
//
//        RelatedStock stock3 = RelatedStock.builder()
//            .scrappedArticle(article)
//            .name("3번")
//            .build();
//
//        relatedStockRepository.save(stock1);
//        relatedStockRepository.save(stock2);
//        relatedStockRepository.save(stock3);
//
//        List<RelatedStock> result = relatedStockRepository.findByScrappedArticle_ScrapId(
//            article.getScrapId());
//
//        assertThat(result).hasSize(3)
//            .contains(stock1, stock2, stock3);
//    }
//}