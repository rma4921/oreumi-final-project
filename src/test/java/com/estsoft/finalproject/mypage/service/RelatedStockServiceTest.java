package com.estsoft.finalproject.mypage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.estsoft.finalproject.mypage.domain.RelatedStock;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.mypage.dto.RelatedStockResponseDTO;
import com.estsoft.finalproject.mypage.repository.RelatedStockRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RelatedStockServiceTest {

    @Mock
    private RelatedStockRepository relatedStockRepository;

    @InjectMocks
    private RelatedStockService relatedStockService;

    @Test
    @DisplayName("스크랩 기사의 관련 주식 가져오기 테스트")
    void findByScrapId() {
        Long scrapId = 1L;

        ScrappedArticle article = ScrappedArticle.builder()
            .scrapId(scrapId)
            .build();

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

        List<RelatedStock> stockList = List.of(stock1, stock2, stock3);

        when(relatedStockRepository.findByScrappedArticle_ScrapId(scrapId))
            .thenReturn(stockList);

        List<RelatedStockResponseDTO> result = relatedStockService.findByScrapId(scrapId);

        assertThat(result).hasSize(3);
        assertThat(result)
            .extracting(RelatedStockResponseDTO::getName)
            .containsExactlyInAnyOrder("1번", "2번", "3번");
    }
}