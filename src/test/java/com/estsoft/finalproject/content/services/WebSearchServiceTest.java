package com.estsoft.finalproject.content.services;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import com.estsoft.finalproject.content.model.dto.NewsSearchItem;
import com.estsoft.finalproject.content.model.dto.ResponseDto;

@SpringBootTest
@ActiveProfiles("test")
public class WebSearchServiceTest {
    @Autowired
    WebSearchService webSearchService;

    @Test
    void testGetSearchResults() {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testGetSearchResults()");
        ResponseDto<List<NewsSearchItem>> res = webSearchService.getSearchResults("국내 주식", 30);
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        logger.info("Size of result is: " + res.getSize());
        res.getItem().forEach(x -> logger.info("Headline is: " + x.headline() + "\n Date is: " + x.timestamp()));
    }
}
