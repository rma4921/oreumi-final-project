package com.estsoft.finalproject.content.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.estsoft.finalproject.content.model.dto.NewsBriefingItem;
import com.estsoft.finalproject.content.model.dto.NewsDetailItem;
import com.estsoft.finalproject.content.model.dto.NewsSearchItem;
import com.estsoft.finalproject.content.model.dto.NewsSummaryItem;
import com.estsoft.finalproject.content.model.dto.ResponseDto;
import com.estsoft.finalproject.content.services.AlanCommunicationService;
import com.estsoft.finalproject.content.services.RecommendationService;
import com.estsoft.finalproject.content.services.WebSearchService;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleResponseDto;
import com.estsoft.finalproject.mypage.service.ScrappedArticleService;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;

@RestController
public class NewsBriefingController {

    private final WebSearchService webSearchService;
    private final AlanCommunicationService alanCommunicationService;
    private final RecommendationService recommendationService;
    private final ScrappedArticleService scrappedArticleService;

    public NewsBriefingController(WebSearchService webSearchService,
        AlanCommunicationService alanCommunicationService,
        RecommendationService recommendationService,
        ScrappedArticleService scrappedArticleService) {
        this.webSearchService = webSearchService;
        this.alanCommunicationService = alanCommunicationService;
        this.recommendationService = recommendationService;
        this.scrappedArticleService = scrappedArticleService;
    }

    @GetMapping("/api/v1/briefing/latest")
    public ResponseEntity<ResponseDto<List<NewsSearchItem>>> latestNewsItems() {
        ResponseDto<List<NewsSearchItem>> responseDto = webSearchService.getSearchResults("국내 주식",
            60, true);
        return ResponseEntity.status(responseDto.getResponseCode()).body(responseDto);
    }

    @GetMapping("/api/v1/briefing/ai_detail")
    public ResponseEntity<ResponseDto<NewsDetailItem>> getDetailedInfo(
        @RequestParam(name = "news-url") String newsUrl) {
        ResponseDto<NewsDetailItem> responseDto = alanCommunicationService.getNewsDetails(newsUrl);
        return ResponseEntity.status(responseDto.getResponseCode()).body(responseDto);
    }

    @GetMapping("/api/v1/briefing/ai_summary")
    public ResponseEntity<ResponseDto<NewsSummaryItem>> getSummaryOnly(
        @RequestParam(name = "news-url") String newsUrl) {
        ResponseDto<NewsSummaryItem> responseDto = alanCommunicationService.onlySummarizeArticle(
            newsUrl);
        return ResponseEntity.status(responseDto.getResponseCode()).body(responseDto);
    }

    @GetMapping("/api/v1/briefing/related-companies")
    public ResponseEntity<ResponseDto<Map<String, String>>> getRelatedCompanies(
        @RequestParam(name = "news-url") String newsUrl) {
        ResponseDto<Map<String, String>> responseDto = alanCommunicationService.findRelatedStock(
            newsUrl, true);
        return ResponseEntity.status(responseDto.getResponseCode()).body(responseDto);
    }

    @GetMapping("/api/v1/briefing/detail")
    public ResponseEntity<ResponseDto<NewsBriefingItem>> getBriefingInfo(
        @RequestParam(name = "news-url") String newsUrl) {
        ResponseDto<NewsBriefingItem> responseDto = webSearchService.getNewsArticleContents(
            newsUrl);
        return ResponseEntity.status(responseDto.getResponseCode()).body(responseDto);
    }

    @GetMapping("/api/v1/briefing/recommendation")
    public ResponseEntity<ResponseDto<String>> getInvestmentRecommendation(
        @RequestParam(name = "company-name") String companyName) {
        ResponseDto<String> responseDto = recommendationService.getInvestmentTacticForCompany(
            companyName);
        return ResponseEntity.status(responseDto.getResponseCode()).body(responseDto);
    }

    @PostMapping("/api/scrap")
    public ResponseEntity<ScrappedArticleResponseDto> scrapAnArticle(
        @AuthenticationPrincipal CustomUsersDetails ud,
        @RequestBody String jsonScrapData) {
        ResponseDto<ScrappedArticleResponseDto> responseDto = scrappedArticleService.scrapAnArticle(
            jsonScrapData, ud);
        return ResponseEntity.status(responseDto.getResponseCode()).body(responseDto.getItem());
    }

}
