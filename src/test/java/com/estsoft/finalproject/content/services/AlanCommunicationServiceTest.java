package com.estsoft.finalproject.content.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import com.estsoft.finalproject.content.model.dto.AlanResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
public class AlanCommunicationServiceTest {
    @Autowired
    private AlanCommunicationService alanCommunicationService;

    @Test
    public void testGetResultFromAlan() {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("getFromAlanTest()");
        AlanResponseDto res = alanCommunicationService.getResultFromAlan("이 기사 자세히 요약해줘: https://n.news.naver.com/mnews/article/215/0001210190");
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.getContent()).isNotEmpty();
        logger.info("Question is: " + res.getQuestion());
        logger.info("Result is: " + res.getContent());
        logger.info("Status is: " + res.getResponseCode());
        logger.info("Time is: " + res.getTimestamp());
    }

    @Test
    void testTranslateTextEnglishToKorean() {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testTranslateTextEnglishToKorean()");
        AlanResponseDto res = alanCommunicationService.translateTextEnglishToKorean("This is a testcase for translating an English sentence into Korean.");
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.getContent()).isNotEmpty();
        logger.info("Question is: " + res.getQuestion());
        logger.info("Result is: " + res.getContent());
        logger.info("Status is: " + res.getResponseCode());
        logger.info("Time is: " + res.getTimestamp());
    }

    @Test
    void testTranslateTextKoreanToEnglish() {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testTranslateTextKoreanToEnglish()");
        AlanResponseDto res = alanCommunicationService.translateTextKoreanToEnglish("한국어에서 영어로 번역하는 테스트입니다.");
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.getContent()).isNotEmpty();
        logger.info("Question is: " + res.getQuestion());
        logger.info("Result is: " + res.getContent());
        logger.info("Status is: " + res.getResponseCode());
        logger.info("Time is: " + res.getTimestamp());
    }

    @Test
    void testFindRelatedStock() {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testFindRelatedStock()");
        AlanResponseDto res = alanCommunicationService.findRelatedStock("한화에어로스페이스", 3);
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.getContent()).isNotEmpty();
        logger.info("Question is: " + res.getQuestion());
        logger.info("Result is: " + res.getContent());
        logger.info("Status is: " + res.getResponseCode());
        logger.info("Time is: " + res.getTimestamp());
    }

    @Test
    public void testSummarizeArticle() {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testSummarizeArticle()");
        AlanResponseDto res = alanCommunicationService.summarizeArticle("https://n.news.naver.com/mnews/article/215/0001210190");
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.getContent()).isNotEmpty();
        logger.info("Question is: " + res.getQuestion());
        logger.info("Result is: " + res.getContent());
        logger.info("Status is: " + res.getResponseCode());
        logger.info("Time is: " + res.getTimestamp());
    }

    @Test
    public void testGetContentsForAPage() throws Exception {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testGetContentsForAPage()");
        AlanResponseDto res = alanCommunicationService.summarizeArticle("https://www.donga.com/news/Politics/article/all/20250526/131671484/2");
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.getContent()).isNotEmpty();
        ObjectMapper mapper = new ObjectMapper();
        String headline = mapper.readTree(res.getContent()).get("headline").asText();
        String content = mapper.readTree(res.getContent()).get("content").asText();
        String topic = mapper.readTree(res.getContent()).get("topic").asText();
        Assertions.assertThat(headline).isNotEmpty();
        Assertions.assertThat(content).isNotEmpty();
        Assertions.assertThat(topic).isNotEmpty();
        AlanResponseDto res2 = alanCommunicationService.findRelatedStock(topic, 3);
        Assertions.assertThat(res2.getResponseCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res2.getContent()).isNotEmpty();
        logger.info("Headline is: " + headline);
        logger.info("Summary is: " + content);
        logger.info("Topic is: " + topic);
        logger.info("Related stocks are: " + res2.getContent());
    }
}
