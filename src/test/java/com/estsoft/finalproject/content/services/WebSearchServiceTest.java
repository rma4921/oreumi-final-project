package com.estsoft.finalproject.content.services;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.estsoft.finalproject.content.model.dto.NewsBriefingItem;
import com.estsoft.finalproject.content.model.dto.NewsSearchItem;
import com.estsoft.finalproject.content.model.dto.ResponseDto;

@SpringBootTest
public class WebSearchServiceTest {
    @Autowired
    WebSearchService webSearchService;

    @Test
    void testGetSearchResults() {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testGetSearchResults()");
        ResponseDto<List<NewsSearchItem>> res = webSearchService.getSearchResults("국내 주식", 60, true);
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        logger.info("Size of result is: " + res.getSize());
        res.getItem().forEach(x -> logger.info("Headline is: " + x.headline() + "\n Date is: " + x.timestamp()));
    }

    @Test
    void testGetNewsContent() {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testGetNewsContent()");
        ResponseDto<NewsBriefingItem> res = webSearchService.getNewsArticleContents("https://n.news.naver.com/mnews/article/018/0006034594?sid=101");
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        logger.info("The headline of the article is: " + res.getItem().headline());
        logger.info("The contents of the article is: " + res.getItem().content());
        logger.info("The timestamp of the article is: " + res.getItem().timestamp());
        logger.info("The category of the article is: " + res.getItem().category());
        logger.info("The response message is: " + res.getMessage());
    }
}
