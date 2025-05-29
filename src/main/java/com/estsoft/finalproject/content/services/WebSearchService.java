package com.estsoft.finalproject.content.services;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.estsoft.finalproject.content.model.dto.ResponseDto;
import com.estsoft.finalproject.content.model.dto.NewsSearchItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WebSearchService {
    private final String NAVER_API_KEY;
    private final String NAVER_API_SECRET;
    private final String SEARCH_URL = "https://openapi.naver.com/v1/search/news.json";
    private final RestClient restClient;

    public WebSearchService(@Value("${com.naver.search.api-key}") String apiKey, @Value("${com.naver.search.api-secret}") String apiSecret) {
        this.restClient = RestClient.create();
        NAVER_API_KEY = apiKey;
        NAVER_API_SECRET = apiSecret;
    }
    
    public ResponseDto<List<NewsSearchItem>> getSearchResults(String query, int numOfItems) {
        String query_encoded = "";
        List<NewsSearchItem> retList = new ArrayList<>();
        try {
            query_encoded = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return ResponseDto.builder(Collections.unmodifiableList(retList)).message("Malformed JSON response!").responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        String r = restClient.get().uri(URI.create(new StringBuilder(SEARCH_URL).append("?query=").append(query_encoded).append("&sort=date&display=").append(numOfItems).toString()))
            .header("X-Naver-Client-Id", NAVER_API_KEY)
            .header("X-Naver-Client-Secret", NAVER_API_SECRET)
            .exchange((req, res) -> res.bodyTo(String.class));
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(r).get("items").forEach(x -> {
                NewsSearchItem currDto = NewsSearchItem.builder()
                    .headline(Optional.ofNullable(x.get("title")).map(JsonNode::asText).orElse("Title node in search result is NULL"))
                    .link(Optional.ofNullable(x.get("originallink")).map(JsonNode::asText).orElse("Original link node in search result is NULL"))
                    .summary(Optional.ofNullable(x.get("description")).map(JsonNode::asText).orElse("Description node in search result is NULL"))
                    .timestamp(Optional.ofNullable(x.get("pubDate")).map(pubDate -> LocalDateTime.parse(pubDate.asText(), DateTimeFormatter.RFC_1123_DATE_TIME)).orElse(LocalDateTime.now()))
                    .build();
                retList.add(currDto);
            });
                return ResponseDto.builder(Collections.unmodifiableList(retList)).message("Search complete!").responseCode(HttpStatus.OK).build();
        } catch (JsonProcessingException ex) {
            return ResponseDto.builder(Collections.unmodifiableList(retList)).message("Malformed JSON response!").responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
