package com.estsoft.finalproject.content.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.estsoft.finalproject.content.model.dto.ResponseDto;
import com.estsoft.finalproject.content.model.dto.StockItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StockPriceService {

    private final RestClient restClient;
    private final String API_KEY;
    private final String STOCK_API_URL = "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo";

    public StockPriceService(@Value("${kr.gov.data.api-key}") String apiKey) {
        this.API_KEY = apiKey;
        this.restClient = RestClient.create();
    }

    public ResponseDto<List<StockItem>> getStockPriceByName(String stockName, int no) {
        return restClient.get()
            .uri(URI.create(new StringBuilder(STOCK_API_URL)
                .append("?numOfRows=").append(no).append("&pageNo=1&resultType=json&")
                .append("serviceKey=").append(API_KEY)
                .append("&itmsNm=").append(stockName).toString())).
            exchange((req, res) -> {
                ArrayList<StockItem> stockList = new ArrayList<>();
                if (res.getStatusCode() == HttpStatus.OK) {
                    try {
                        String r = res.bodyTo(String.class);
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.readTree(r).get("response").get("body").
                            get("items").
                            get("item").forEach(x -> {
                                String date = Optional.ofNullable(x.get("basDt")).map(JsonNode::asText)
                                    .orElseThrow(() -> new IllegalArgumentException());
                                String name = Optional.ofNullable(x.get("itmsNm")).map(JsonNode::asText)
                                    .orElseThrow(() -> new IllegalArgumentException());
                                Long price = Long.valueOf(
                                    Optional.ofNullable(x.get("clpr")).map(JsonNode::asText)
                                        .orElseThrow(() -> new IllegalArgumentException()));
                                Double fltRate = Double.valueOf(
                                    Optional.ofNullable(x.get("fltRt")).map(JsonNode::asText)
                                        .orElseThrow(() -> new IllegalArgumentException()));
                                String isinCd = Optional.ofNullable(x.get("isinCd"))
                                    .map(JsonNode::asText)
                                    .orElseThrow(() -> new IllegalArgumentException());
                                stockList.add(
                                    StockItem.builder().name(name).fluctuationRate(fltRate).isin(isinCd)
                                        .price(price).baseDate(date).build());
                            });
                    } catch (JsonProcessingException jex) {
                        return ResponseDto.builder(Collections.unmodifiableList(stockList))
                            .message("Error processing JSON data")
                            .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    } catch (NumberFormatException nex) {
                        return ResponseDto.builder(Collections.unmodifiableList(stockList))
                            .message("Error parsing stock price data")
                            .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    } catch (IllegalArgumentException ilex) {
                        return ResponseDto.builder(Collections.unmodifiableList(stockList))
                            .message("Error parsing items from JSON data")
                            .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                    return ResponseDto.builder(Collections.unmodifiableList(stockList))
                        .message(stockList.size() + "items").responseCode(HttpStatus.OK).build();
                }
                return ResponseDto.builder(Collections.unmodifiableList(stockList))
                    .message("An error occurred while fetching information from the server")
                    .responseCode(res.getStatusCode()).build();
            });
    }

    public ResponseDto<List<StockItem>> getStockPrice(String isin, int no) {
        return restClient.get()
            .uri(URI.create(new StringBuilder(STOCK_API_URL)
                .append("?numOfRows=").append(no).append("&pageNo=1&resultType=json&")
                .append("serviceKey=").append(API_KEY)
                .append("&isinCd=").append(isin).toString())).
            exchange((req, res) -> {
                ArrayList<StockItem> stockList = new ArrayList<>();
                if (res.getStatusCode() == HttpStatus.OK) {
                    try {
                        String r = res.bodyTo(String.class);
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.readTree(r).get("response").get("body").
                            get("items").
                            get("item").forEach(x -> {
                                String date = Optional.ofNullable(x.get("basDt")).map(JsonNode::asText)
                                    .orElseThrow(() -> new IllegalArgumentException());
                                String name = Optional.ofNullable(x.get("itmsNm")).map(JsonNode::asText)
                                    .orElseThrow(() -> new IllegalArgumentException());
                                Long price = Long.valueOf(
                                    Optional.ofNullable(x.get("clpr")).map(JsonNode::asText)
                                        .orElseThrow(() -> new IllegalArgumentException()));
                                Double fltRate = Double.valueOf(
                                    Optional.ofNullable(x.get("fltRt")).map(JsonNode::asText)
                                        .orElseThrow(() -> new IllegalArgumentException()));
                                String isinCd = Optional.ofNullable(x.get("isinCd"))
                                    .map(JsonNode::asText)
                                    .orElseThrow(() -> new IllegalArgumentException());
                                stockList.add(
                                    StockItem.builder().name(name).fluctuationRate(fltRate).isin(isinCd)
                                        .price(price).baseDate(date).build());
                            });
                    } catch (JsonProcessingException jex) {
                        return ResponseDto.builder(Collections.unmodifiableList(stockList))
                            .message("Error processing JSON data")
                            .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    } catch (NumberFormatException nex) {
                        return ResponseDto.builder(Collections.unmodifiableList(stockList))
                            .message("Error parsing stock price data")
                            .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    } catch (IllegalArgumentException ilex) {
                        return ResponseDto.builder(Collections.unmodifiableList(stockList))
                            .message("Error parsing items from JSON data")
                            .responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                    return ResponseDto.builder(Collections.unmodifiableList(stockList))
                        .message(stockList.size() + "items").responseCode(HttpStatus.OK).build();
                }
                return ResponseDto.builder(Collections.unmodifiableList(stockList))
                    .message("An error occurred while fetching information from the server")
                    .responseCode(res.getStatusCode()).build();
            });
    }


}
