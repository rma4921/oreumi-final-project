package com.estsoft.finalproject.content.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.estsoft.finalproject.content.model.dto.AlanResponseDto;
import com.estsoft.finalproject.content.model.dto.NewsDetailItem;
import com.estsoft.finalproject.content.model.dto.ResponseDto;
import com.estsoft.finalproject.content.prompting.SimpleAlanKoreanPromptBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AlanCommunicationService {
    private final RestClient restClient;
    //private final ViewedNewsItemRepository viewedNewsItemRepository;
    private final String ALAN_URL = "https://kdt-api-function.azurewebsites.net/api/v1/question";
    private final String ALAN_API_KEY;
    
    public AlanCommunicationService(@Value("${ai.est.alan.client-id}") String apiKey, StockPriceService stockPriceService /* ,ViewedNewsItemRepository viewedNewsItemRepository */) {
        this.restClient = RestClient.create();
        //this.viewedNewsItemRepository = viewedNewsItemRepository;
        ALAN_API_KEY = apiKey;
    }

    public AlanResponseDto getResultFromAlan(String question) {
        String question_encoded = "";
        try {
            question_encoded = URLEncoder.encode(question, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new AlanResponseDto(question, "An error occurred while decoding your question: " + e.getMessage(), LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return restClient.get().
            uri(URI.create(new StringBuilder(ALAN_URL).append("?content=").append(question_encoded).append("&client_id=").append(ALAN_API_KEY).toString())).
            exchange((req, res) -> { 
                StringBuilder resBuilder = new StringBuilder();
                BufferedReader br = Optional.of(res.getBody()).map(body -> new BufferedReader(new InputStreamReader(body))).get();
                while (true) {
                    int i = br.read();
                    if (i < 0) {
                        break;
                    }
                    resBuilder.append((char) i);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> jsonRes = objectMapper.readValue(resBuilder.toString(), new TypeReference<Map<String, Object>>(){}); 
                String ret = jsonRes.getOrDefault("content", "").toString();
                if (ret.isEmpty()) {
                    return new AlanResponseDto(question, "An error occurred while parsing the response from Alan", LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    return new AlanResponseDto(question, ret, LocalDateTime.now(), res.getStatusCode());
                }
            });
    }

    public AlanResponseDto translateTextKoreanToEnglish(String textToTranslate) {
        String translateQuery = new StringBuilder("다음 문장 영어로 번역해줘: \"").append(textToTranslate).append("\"").toString();
        return getResultFromAlan(translateQuery);
    }

    public AlanResponseDto translateTextEnglishToKorean(String textToTranslate) {
        String translateQuery = new StringBuilder("다음 문장 한국어로 번역해줘: \"").append(textToTranslate).append("\"").toString();
        return getResultFromAlan(translateQuery);
    }

    public AlanResponseDto findRelatedStock(String topic, int numberOfRelatedCompanies) {
        String translateQuery = new StringBuilder("다음 주제와 관련된 주식에 대한 정보 찾아줘. (최대 ").append(numberOfRelatedCompanies).append("개) : \"")
            .append(topic)
            .append("\"")
            .append(" 상장되지 않았거나, 정보를 찾을 수 없는 회사는 생략해도 돼. ")
            .append(" 답변은 다음 형식으로 해줘: \"")
            .append("[{ \"company\" : (회사명), \"stock_price\" : (현재 주가) }]").toString();
        return getResultFromAlan(translateQuery);
    }

    public AlanResponseDto summarizeArticle(String articleUrl) {
        String translateQuery = new StringBuilder("이 URL에 있는 기사 자세히 요약해줘: ")
            .append(articleUrl)
            .append(" 그리고 이 기사의 대략적인 주제를 확인해 주고, 기사를 <정치, 경제, 사회, 생활문화, IT과학, 세계> 중에서 가장 적절한 카테고리로 분류해 줘.")
            .append(" 그리고 이 기사와 관련된 한국 회사들을 찾아 줘. 회사들은 한국 증시에 상장된 회사들만 찾아 줘.")
            .append(" 답변은 다음 형식으로 해줘: \"")
            .append("{ \"headline\" : (제목), \"content\" : (자세한 요약), \"category\" : (카테고리), \"topic\" : (대략적인 주제), \"companies\"  : [{\"company_name\"  : (회사 이름), \"investment_opinion\"  : (투자의견)}] }").toString();
        return getResultFromAlan(translateQuery);
    }

    public AlanResponseDto getInvestmentTactic(List<String> listOfArticles, List<String> listOfInterestedStocks) {
        StringBuilder articles = new StringBuilder();
        StringBuilder interestedStocks = new StringBuilder();
        listOfArticles.stream().map(x -> "<" + x + "> ").forEach(articles::append);
        listOfArticles.stream().map(x -> "<" + x + "> ").forEach(interestedStocks::append);
        return getResultFromAlan(SimpleAlanKoreanPromptBuilder.start().addCommand("가장 잘 맞는 투자전략을 추천해줘.")
        .addContext("최근에 읽은 기사들은 다음과 같아: " + articles.toString())
        .addContext("최근에 관심을 가진 종목들은 다움과 같아: " + interestedStocks.toString())
        .buildPrompt());
    }

    public AlanResponseDto sortTopic(String url) {
        return getResultFromAlan(SimpleAlanKoreanPromptBuilder.start().addCommand("이 URL의 있는 기사는 어떤 카테고리로 분류하는 것이 가장 알맞을까?: ").addCommand(url)
        .addContext("카테고리의 목록은 다음과 같아. 다음 중 하나로 분류해줘: 정치, 경제, 사회, 생활문화, IT과학, 세계")
        .setOutputFormat("{ \"category\" : (분류한 카테고리), \"message\" : (분류한 이유) }")
        .addErrorHandler("{ \"category\" : \"알 수 없음\", \"message\" : (분류하지 못한 이유) }")
        .buildPrompt());
    }

    public ResponseDto<NewsDetailItem> getNewsDetails(String articleUrl) {
        AlanResponseDto newsSearchItem = summarizeArticle(articleUrl);
        if (newsSearchItem.getResponseCode() != HttpStatus.OK) {
            NewsDetailItem ret = NewsDetailItem.builder().headline("Invalid response due to an error").content("").category("").topic("").link(articleUrl).build();
            return ResponseDto.builder(ret).message("Invalid response due to an error").responseCode(newsSearchItem.getResponseCode()).build();
        }
        ObjectMapper mapper = new ObjectMapper();
        String r = newsSearchItem.getContent();
        if (!r.startsWith("{") || !r.endsWith("}")) {
            int startJson = r.indexOf('{');
            int endJson = r.lastIndexOf('}');
            r = r.substring(startJson, endJson + 1);
        }
        try {
            JsonNode jsonRead = mapper.readTree(r);
            String headline = Optional.ofNullable(jsonRead.get("headline")).map(x -> x.asText("null")).orElse("null");
            String content = Optional.ofNullable(jsonRead.get("content")).map(x -> x.asText("null")).orElse("null");
            String topic = Optional.ofNullable(jsonRead.get("topic")).map(x -> x.asText("null")).orElse("null");
            String category = Optional.ofNullable(jsonRead.get("category")).map(x -> x.asText("null")).orElse("null");
            Optional<JsonNode> companies = Optional.ofNullable(jsonRead.get("companies"));
            
            if (headline.equalsIgnoreCase("null") || 
                content.equalsIgnoreCase("null") ||
                category.equalsIgnoreCase("null") || 
                companies.isEmpty() ||
                topic.equalsIgnoreCase("null")) {
                NewsDetailItem ret = NewsDetailItem.builder().headline("Malformed JSON. Failed to parse!").content(r).category("").topic("").link(articleUrl).build();
                return ResponseDto.builder(ret).message("Invalid response due to an error").responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            Map<String, String> companyList = new LinkedHashMap<>();
            companies.get().forEach(x -> companyList.put(x.get("company_name").asText(), x.get("investment_opinion").asText()));
            NewsDetailItem ret = NewsDetailItem.builder().headline(headline).content(content).topic(topic).category(category).relatedCompanies(Collections.unmodifiableMap(companyList)).link(articleUrl).build();
            return ResponseDto.builder(ret).message("Successfully summarized the article with details.").responseCode(HttpStatus.OK).build();
        } catch (JsonProcessingException jex) {
            NewsDetailItem ret = NewsDetailItem.builder().headline("Malformed JSON. Failed to parse!").content(r).category("").topic("").link(articleUrl).build();
            return ResponseDto.builder(ret).message("Invalid response due to an error").responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
