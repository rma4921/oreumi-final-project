package com.estsoft.finalproject.content.services;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import com.estsoft.finalproject.content.model.dto.AlanResponseDto;
import com.estsoft.finalproject.content.prompting.SimpleAlanKoreanPromptBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
        AlanResponseDto res = alanCommunicationService.summarizeArticle("https://n.news.naver.com/mnews/article/018/0006034594?sid=101");
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
        AlanResponseDto res = alanCommunicationService.summarizeArticle("https://n.news.naver.com/mnews/article/018/0006034594?sid=101");
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        String r = res.getContent();
        Assertions.assertThat(r).isNotEmpty();
        if (!res.getContent().startsWith("{") || !res.getContent().endsWith("}")) {
            int startJson = r.indexOf('{');
            int endJson = r.lastIndexOf('}');
            r = r.substring(startJson, endJson + 1);
        }
        logger.info("Response is: " + r);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode r_node = mapper.readTree(r);
        String headline = r_node.get("headline").asText();
        String content = r_node.get("content").asText();
        String topic = r_node.get("topic").asText();
        String category = r_node.get("category").asText();
        JsonNode companies = r_node.get("companies");
        StringBuilder company_list = new StringBuilder();
        Assertions.assertThat(headline).isNotEmpty();
        Assertions.assertThat(content).isNotEmpty();
        Assertions.assertThat(topic).isNotEmpty();
        Assertions.assertThat(category).isNotEmpty();
        Assertions.assertThat(companies.isArray()).isTrue();
        companies.forEach(x -> company_list.append("[ Company Name: " + x.get("company_name").asText() + " ]"));
        logger.info("Headline is: " + headline);
        logger.info("Summary is: " + content);
        logger.info("Topic is: " + topic);
        logger.info("Related stocks are: " + company_list.toString());
    }

    @Test
    public void testSimpleAlanPromptBuilder() {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testSimpleAlanPromptBuilder()");
        AlanResponseDto res = alanCommunicationService.getResultFromAlan(SimpleAlanKoreanPromptBuilder.start().
            addCommand("다음 기사 요약해줘: https://n.news.naver.com/mnews/article/018/0006034594?sid=101")
            .setOutputFormat("{ \"title\" : (기사 제목), \"summary\" : (요약문) }")
            .addErrorHandler("다음과 같이 결과를 출력해줘: { \"title\" : \"오류\", \"summary\" : (오류 발생 이유) }")
            .buildPrompt());
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        logger.info("Response is: " + res.getContent());
    }

    @Test
    public void testSimpleAlanPromptBuilderWithContext() {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testSimpleAlanPromptBuilder()");
        AlanResponseDto res = alanCommunicationService.getResultFromAlan(SimpleAlanKoreanPromptBuilder.start()
            .addCommand("맛집 5곳 추천해줘")
            .addContext("수원시 인계동에 있는 곳. 종류는 한식으로.")
            .setOutputFormat("[{ \"name\" : (이름), \"address\" : (주소) }]")
            .addErrorHandler("다음과 같이 결과를 출력해줘: [{ \"name\" : \"찾을 수 없음\", \"address\" : (찾을 수 없었던 이유) }]")
            .buildPrompt());
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        logger.info("Response is: " + res.getContent());
    }

    @Test
    public void testWrongPromptBuilder() throws JsonProcessingException {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testSimpleAlanPromptBuilder()");
        AlanResponseDto res = alanCommunicationService.getResultFromAlan(SimpleAlanKoreanPromptBuilder.start().
            addCommand("다음 기사 요약해줘: https://www.donga.com/newssdfsdfsdf/Politisdfsdfsdfcs/article/all/20250526/131671484/2")
            .setOutputFormat("{ \"title\" : (기사 제목), \"summary\" : (요약문) }")
            .addErrorHandler("다음과 같이 결과를 출력해줘: { \"title\" : \"오류\", \"summary\" : (오류 발생 이유) }")
            .buildPrompt());
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        logger.info("Response is: " + res.getContent());
        ObjectMapper mapper = new ObjectMapper();
        String errorTitle = mapper.readTree(res.getContent()).get("title").asText();
        Assertions.assertThat(errorTitle).isEqualTo("오류");
    }

    @Test
    public void testGetTopic() throws JsonProcessingException {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testSimpleAlanPromptBuilder()");
        AlanResponseDto res1 = alanCommunicationService.sortTopic("https://www.donga.com/news/Politics/article/all/20250526/131671484/2");
        AlanResponseDto res2 = alanCommunicationService.sortTopic("https://n.news.naver.com/mnews/article/215/0001210190");
        Assertions.assertThat(res1.getResponseCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res2.getResponseCode()).isEqualTo(HttpStatus.OK);
        logger.info("Response is: " + res1.getContent());
        logger.info("Response is: " + res2.getContent());
    }

    @Test
    public void testGetInvestmentTactic() throws JsonProcessingException {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testGetInvestmentTactic()");
        List<String> articles = List.of("KAI, 필리핀에 FA-50 12대 추가 수출 계약 체결…1조원 규모", 
            "[HJ중공업 부활] 수주 포트폴리오 다변화 '조선 방산·건설' 재정비", 
            "필리핀에 FA-50 12대 추가 수출 계약…1조원 규모", 
            "트럼프발 철강 50% ‘관세폭탄’ 발효… 韓 철강 어쩌나", 
            "'D램 왕좌' 내준 삼성…2나노 전쟁에 운명");
        List<String> stocks = List.of("한화에어로스페이스", "KAI", "한화시스템", "삼성전자", "LIG넥스원");
        AlanResponseDto res = alanCommunicationService.getInvestmentTactic(articles, stocks);
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
        logger.info("Response is: " + res.getContent());
    }
}
