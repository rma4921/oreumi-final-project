package com.estsoft.finalproject.content.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.estsoft.finalproject.content.model.dto.ResponseDto;
import com.estsoft.finalproject.content.model.dto.StockItem;
import com.estsoft.finalproject.content.services.StockPriceService;

@RestController
public class StockInfoController {
    private final StockPriceService stockPriceService;

    public StockInfoController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    @GetMapping("/api/v1/stock-price/by-name")
    public ResponseEntity<ResponseDto<List<StockItem>>> getStockItemsByName(@RequestParam(name = "name") String name, @RequestParam(name = "count") int count) {
        ResponseDto<List<StockItem>> responseDto = stockPriceService.getStockPriceByName(name, count);        
        return ResponseEntity.status(responseDto.getResponseCode()).body(responseDto);
    }

    @GetMapping("/api/v1/stock-price/by-isin")
    public ResponseEntity<ResponseDto<List<StockItem>>> getStockItemsByIsin(@RequestParam(name = "isin") String isin, @RequestParam(name = "count") int count) {
        ResponseDto<List<StockItem>> responseDto = stockPriceService.getStockPriceByName(isin, count);        
        return ResponseEntity.status(responseDto.getResponseCode()).body(responseDto);
    }

    //@GetMapping("/api/personal/strategy")
    //public ResponseEntity<String> getInvestmentStrategy() {
    //    return new String();
    //}
    
}