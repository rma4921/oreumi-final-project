package com.estsoft.finalproject.mypage.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.estsoft.finalproject.mypage.domain.RelatedStock;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.mypage.dto.RelatedStockResponseDTO;
import com.estsoft.finalproject.mypage.repository.RelatedStockRepository;
import com.estsoft.finalproject.mypage.repository.ScrappedArticleRepository;
import com.estsoft.finalproject.user.User;
import com.estsoft.finalproject.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class RelatedStockServiceTest {

    @Autowired
    private RelatedStockRepository relatedStockRepository;

    @Autowired
    private RelatedStockService relatedStockService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScrappedArticleRepository scrappedArticleRepository;

    @Test
    @DisplayName("스크랩 기사의 관련 주식 가져오기 테스트")
    void findByScrapId() {
        User tester = new User();
        tester.updateNickname("tester");
        userRepository.save(tester);

        ScrappedArticle article = ScrappedArticle.builder()
            .user(tester)
            .scrapDate(LocalDateTime.now())
            .title("테스트 중입니다.")
            .link("https://www.google.com")
            .description("테스트용 더미데이터입니다.")
            .pubDate(LocalDateTime.now())
            .topic("test")
            .build();
        scrappedArticleRepository.save(article);

        RelatedStock stock1 = RelatedStock.builder()
            .scrappedArticle(article)
            .name("1번")
            .build();
        RelatedStock stock2 = RelatedStock.builder()
            .scrappedArticle(article)
            .name("2번")
            .build();
        RelatedStock stock3 = RelatedStock.builder()
            .scrappedArticle(article)
            .name("3번")
            .build();

        relatedStockRepository.save(stock1);
        relatedStockRepository.save(stock2);
        relatedStockRepository.save(stock3);

        List<RelatedStockResponseDTO> result = relatedStockService.findByScrapId(article.getScrapId());

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo(stock1.getName());
        assertThat(result.get(1).getName()).isEqualTo(stock2.getName());
        assertThat(result.get(2).getName()).isEqualTo(stock3.getName());
    }
}