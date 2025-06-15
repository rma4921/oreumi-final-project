package com.estsoft.finalproject.content.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.estsoft.finalproject.content.model.dto.AlanResponseDto;
import com.estsoft.finalproject.content.model.dto.NewsSearchItem;
import com.estsoft.finalproject.content.model.dto.ResponseDto;
import com.estsoft.finalproject.content.model.dto.StockItem;
import com.estsoft.finalproject.content.prompting.SimpleAlanKoreanPromptBuilder;

@Service
public class RecommendationService {
    private final AlanCommunicationService alanCommunicationService;
    private final StockPriceService stockPriceService;
    private final WebSearchService webSearchService;

    public RecommendationService(AlanCommunicationService alanCommunicationService, StockPriceService stockPriceService, WebSearchService webSearchService) {
        this.alanCommunicationService = alanCommunicationService;
        this.stockPriceService = stockPriceService;
        this.webSearchService = webSearchService;
    }

    public ResponseDto<String> getInvestmentTacticForCompany(String companyName) {
        ResponseDto<List<StockItem>> stockPriceData = stockPriceService.getStockPriceByName(companyName, 30);
        ResponseDto<List<NewsSearchItem>> newsItems = webSearchService.getSearchResults(companyName, 5, false);

        StringBuilder stockPriceString = new StringBuilder();
        if (stockPriceData.getResponseCode() == HttpStatus.OK && stockPriceData.getSize() != 0) {
            stockPriceData.getItem().forEach(x -> { if (x.name().trim().equalsIgnoreCase(companyName)) { stockPriceString.append(" [ 일자: " + x.price() + " 주가: " + x.price() + " 전일대비 증감률: " + x.fluctuationRate() + "% ] "); } });
        }
        StringBuilder newsItemsForCompany = new StringBuilder();
        if (newsItems.getResponseCode() == HttpStatus.OK && newsItems.getSize() != 0) {
            newsItems.getItem().forEach(x -> { newsItemsForCompany.append(" [ 제목: <" + x.headline() + "> 날짜: <" + x.timestamp() + "> ] "); });
        }
        
        AlanResponseDto rez = alanCommunicationService.getResultFromAlan(SimpleAlanKoreanPromptBuilder.start().
            addCommand("다음 회사에 대한 투자정보를 추천해 줘. 인터넷에서 검색할 수 있는 내용들도 참고해서 추천해 줘.").
            addContext("회사의 이름은 [" + companyName + "] 이야.").
            addContext("지난 30일간 해당 회사의 주가는 다음과 같아: [ " + stockPriceString.toString() + "]").
            addContext("최근 해당 회사와 관련된 뉴스기사들은 다음과 같아: " + newsItemsForCompany.toString() + " ]").buildPrompt());
        return ResponseDto.builder(rez.getContent()).message("The investment recommendation for the company is as follows.").responseCode(rez.getResponseCode()).build();
    }


}
