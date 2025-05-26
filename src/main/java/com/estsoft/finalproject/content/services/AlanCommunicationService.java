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
import com.fasterxml.jackson.core.type.TypeReference;
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

    public AlanResponseDto findRelatedStock(String company) {
        String translateQuery = new StringBuilder("다음 회사와 관련된 주식 찾아줘: \"").append(company).append("\"").toString();
        return getResultFromAlan(translateQuery);
    }

}
