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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.estsoft.finalproject.content.model.dto.ResponseDto;
import com.estsoft.finalproject.content.model.dto.NewsBriefingItem;
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

    public WebSearchService(@Value("${com.naver.search.api-key}") String apiKey,
                            @Value("${com.naver.search.api-secret}") String apiSecret,
                            RestClient restClient) {
        this.NAVER_API_KEY = apiKey;
        this.NAVER_API_SECRET = apiSecret;
        this.restClient = restClient;
    }

    public ResponseDto<List<NewsSearchItem>> getSearchResults(String query, int numOfItems,
        boolean naverOnly) {
        String query_encoded = "";
        List<NewsSearchItem> retList = new ArrayList<>();
        try {
            query_encoded = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return ResponseDto.builder(Collections.unmodifiableList(retList))
                .message("Malformed JSON response!").responseCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
        String r = restClient.get().uri(URI.create(
                new StringBuilder(SEARCH_URL).append("?query=").append(query_encoded)
                    .append("&sort=date&display=").append(numOfItems).toString()))
            .header("X-Naver-Client-Id", NAVER_API_KEY)
            .header("X-Naver-Client-Secret", NAVER_API_SECRET)
            .exchange((req, res) -> res.bodyTo(String.class));
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(r).get("items").forEach(x -> {
                NewsSearchItem currDto = NewsSearchItem.builder()
                    .headline(Optional.ofNullable(x.get("title")).map(JsonNode::asText)
                        .orElse("Title node in search result is NULL"))
                    .link(Optional.ofNullable(x.get("originallink")).map(JsonNode::asText)
                        .orElse("Original link node in search result is NULL"))
                    .refLink(Optional.ofNullable(x.get("link")).map(JsonNode::asText)
                        .orElse("Llink node in search result is NULL"))
                    .summary(Optional.ofNullable(x.get("description")).map(JsonNode::asText)
                        .orElse("Description node in search result is NULL"))
                    .timestamp(Optional.ofNullable(x.get("pubDate")).map(
                        pubDate -> LocalDateTime.parse(pubDate.asText(),
                            DateTimeFormatter.RFC_1123_DATE_TIME)).orElse(LocalDateTime.now()))
                    .build();
                if (naverOnly) {
                    if (currDto.refLink().contains("news.naver.com")) {
                        retList.add(currDto);
                    }
                } else {
                    retList.add(currDto);
                }
            });
            return ResponseDto.builder(Collections.unmodifiableList(retList))
                .message("Search complete!").responseCode(HttpStatus.OK).build();
        } catch (JsonProcessingException ex) {
            return ResponseDto.builder(Collections.unmodifiableList(retList))
                .message("Malformed JSON response!").responseCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    public ResponseDto<NewsBriefingItem> getNewsArticleContents(String url) {
        NewsBriefingItem errorItem = new NewsBriefingItem("", "", "", LocalDateTime.now());
        try {
            String category = "";
            if (url.contains("sid=") && !url.endsWith("&sid=")) {
                switch (url.split("sid=")[1]) {
                    case "100":
                        category = "정치";
                        break;
                    case "101":
                        category = "경제";
                        break;
                    case "102":
                        category = "사회";
                        break;
                    case "103":
                        category = "생활문화";
                        break;
                    case "104":
                        category = "세계";
                        break;
                    case "105":
                        category = "IT과학";
                        break;
                    case "106":
                        category = "생활문화";
                        break;
                    case "107":
                        category = "생활문화";
                        break;
                    default:
                        category = "";
                        break;
                }
            }
            ResponseSpec res = restClient.get().uri(URI.create(url))
                .retrieve();
            String r = res.body(String.class);
            StringBuilder retBuilder = new StringBuilder();
            if (!r.contains("data-date-time=\"")) {
                return ResponseDto.builder(errorItem)
                    .message("News article's time data is not valid (no time delimiter)")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            String[] dateTimeSplit = r.split("data-date-time=\"");
            if (dateTimeSplit.length < 2) {
                return ResponseDto.builder(errorItem)
                    .message("News article's time data is not valid (nothing after time delimiter)")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            if (!dateTimeSplit[1].contains("\"")) {
                return ResponseDto.builder(errorItem).message(
                        "News article's time data is not valid (timestamp quotation not complete)")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            String dateTimeString = dateTimeSplit[1].split("\"")[0];
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String[] s1 = r.split("id=\"dic_area\"");
            if (s1.length < 2) {
                return ResponseDto.builder(errorItem).message("News article data is not valid")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            String[] s2 = s1[1].split("/article>");
            if (s1.length < 1) {
                return ResponseDto.builder(errorItem).message("News article data is not valid")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            String[] s3 = s2[0].split(">");
            for (String sc : s3) {
                if (sc.contains("<")) {
                    String[] scs = sc.split("<");
                    if (scs.length != 0) {
                        retBuilder.append(scs[0].replaceAll("\t", "").trim());
                        retBuilder.append("\n");
                    }
                }
            }
            if (!r.contains("id=\"title_area\"")) {
                return ResponseDto.builder(errorItem)
                    .message("News article data does not contain valid headline data")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            String[] headline1 = r.split("id=\"title_area\"");
            if (headline1.length < 2) {
                return ResponseDto.builder(errorItem).message(
                        "News article data does not contain valid headline data (invalid headline node)")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            if (!headline1[1].contains("/h2>")) {
                return ResponseDto.builder(errorItem).message(
                        "News article data does not contain valid headline data (invalid h2 tag)")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            String headline2 = headline1[1].split("/h2>")[0];
            if (!headline2.contains("<")) {
                return ResponseDto.builder(errorItem).message(
                        "News article data does not contain valid headline data (invalid h2 tag)")
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            String[] headline3 = headline2.split(">");
            StringBuilder headlineBuilder = new StringBuilder();
            for (String sc : headline3) {
                if (sc.contains("<")) {
                    String[] scs = sc.split("<");
                    if (scs.length != 0) {
                        headlineBuilder.append(scs[0].trim());
                    }
                }
            }
            return ResponseDto.builder(
                new NewsBriefingItem(category, headlineBuilder.toString(), retBuilder.toString(),
                    dateTime)).message("Request complete").responseCode(HttpStatus.OK).build();
        } catch (HttpServerErrorException ex) {
            return ResponseDto.builder(errorItem).message(ex.getLocalizedMessage())
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (HttpClientErrorException ex) {
            return ResponseDto.builder(errorItem).message(ex.getLocalizedMessage())
                .responseCode(HttpStatus.BAD_REQUEST).build();
        }
    }
}
