package com.estsoft.finalproject.content.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.estsoft.finalproject.content.model.dto.NewsDetailItem;
import com.estsoft.finalproject.content.model.dto.NewsSearchItem;
import com.estsoft.finalproject.content.model.dto.ResponseDto;
import com.estsoft.finalproject.content.services.AlanCommunicationService;
import com.estsoft.finalproject.content.services.WebSearchService;

@RestController
public class NewsBriefingController {
    private final WebSearchService webSearchService;
    private final AlanCommunicationService alanCommunicationService;

    public NewsBriefingController(WebSearchService webSearchService, AlanCommunicationService alanCommunicationService) {
        this.webSearchService = webSearchService;
        this.alanCommunicationService = alanCommunicationService;
    }

    @GetMapping("/api/briefing/latest")
    public ResponseEntity<ResponseDto<List<NewsSearchItem>>> latestNewsItems() {
        ResponseDto<List<NewsSearchItem>> responseDto = webSearchService.getSearchResults("국내 주식", 30);
        return ResponseEntity.status(responseDto.getResponseCode()).body(responseDto);
    }

    @GetMapping("/api/briefing/detail")
    public ResponseEntity<ResponseDto<NewsDetailItem>> getDetailedInfo(@RequestParam(name = "news-url") String newsUrl) {
        ResponseDto<NewsDetailItem> responseDto = alanCommunicationService.getNewsDetails(newsUrl);
        return ResponseEntity.status(responseDto.getResponseCode()).body(responseDto);
    }
}
