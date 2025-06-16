package com.estsoft.finalproject.content.services;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.estsoft.finalproject.content.model.dto.ResponseDto;
import com.estsoft.finalproject.content.model.dto.StockItem;

@SpringBootTest
public class StockPriceServiceTest {
    @Autowired
    StockPriceService stockPriceService;

    @Test
    void testGetStockPrice() {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testGetStockPrice()");
        ResponseDto<List<StockItem>> res = stockPriceService.getStockPrice("KR7012450003", 30);
        res.getItem().forEach(x -> logger.info("Name: " + x.name() + " Price: " + String.valueOf(x.price()) + "ISIN: " + x.isin() + " Date: " + x.baseDate()));
        logger.info(res.getMessage());
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testGetStockPriceByName() {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("testGetStockPriceByName()");
        ResponseDto<List<StockItem>> res = stockPriceService.getStockPriceByName("한화에어로스페이스", 30);
        res.getItem().forEach(x -> logger.info("Name: " + x.name() + " Price: " + String.valueOf(x.price()) + "ISIN: " + x.isin() + " Date: " + x.baseDate()));
        logger.info(res.getMessage());
        Assertions.assertThat(res.getResponseCode()).isEqualTo(HttpStatus.OK);
    }
}
