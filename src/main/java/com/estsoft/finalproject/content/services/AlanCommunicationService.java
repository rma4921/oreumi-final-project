package com.estsoft.finalproject.content.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.estsoft.finalproject.content.model.dto.AlanResponseDto;
import com.estsoft.finalproject.content.model.dto.NewsDetailItem;
import com.estsoft.finalproject.content.model.dto.ResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AlanCommunicationService {
    private final RestClient restClient;
    private final String ALAN_URL = "https://kdt-api-function.azurewebsites.net/api/v1/question";
    private final String ALAN_API_KEY;
    
    public AlanCommunicationService(@Value("${ai.est.alan.client-id}") String apiKey) {
        this.restClient = RestClient.create();
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
            .append(" 만약 회사가 언급되었으면, 어떤 회사가 언급되었는지 주제에 명시해 줘. ")
            .append(" 답변은 다음 형식으로 해줘: \"")
            .append("{ \"headline\" : (제목), \"content\" : (요약), topic : (주제) }").toString();
        return getResultFromAlan(translateQuery);
    }

    public ResponseDto<NewsDetailItem> getNewsDetails(String articleUrl) {
        AlanResponseDto newsSearchItem = summarizeArticle(articleUrl);
        if (newsSearchItem.getResponseCode() != HttpStatus.OK) {
            NewsDetailItem ret = NewsDetailItem.builder().headline("Invalid response due to an error").content("").timestamp(newsSearchItem.getTimestamp()).topic("").link(articleUrl).build();
            return ResponseDto.builder(ret).message("Invalid response due to an error").responseCode(newsSearchItem.getResponseCode()).build();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonRead = mapper.readTree(newsSearchItem.getContent());
            String headline = Optional.ofNullable(jsonRead.get("headline")).map(x -> x.asText("null")).orElse("null");
            String content = Optional.ofNullable(jsonRead.get("content")).map(x -> x.asText("null")).orElse("null");
            String topic = Optional.ofNullable(jsonRead.get("topic")).map(x -> x.asText("null")).orElse("null");
            if (headline.equalsIgnoreCase("null") || content.equalsIgnoreCase("null") || topic.equalsIgnoreCase("null")) {
                NewsDetailItem ret = NewsDetailItem.builder().headline("Malformed JSON. Failed to parse!").content(newsSearchItem.getContent()).timestamp(newsSearchItem.getTimestamp()).topic("").link(articleUrl).build();
                return ResponseDto.builder(ret).message("Invalid response due to an error").responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            NewsDetailItem ret = NewsDetailItem.builder().headline(headline).content(content).topic(topic).timestamp(newsSearchItem.getTimestamp()).link(articleUrl).build();
            return ResponseDto.builder(ret).message("Successfully summarized the article with details.").responseCode(HttpStatus.OK).build();
        } catch (JsonProcessingException jex) {
            NewsDetailItem ret = NewsDetailItem.builder().headline("Malformed JSON. Failed to parse!").content(newsSearchItem.getContent()).timestamp(newsSearchItem.getTimestamp()).topic("").link(articleUrl).build();
            return ResponseDto.builder(ret).message("Invalid response due to an error").responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
