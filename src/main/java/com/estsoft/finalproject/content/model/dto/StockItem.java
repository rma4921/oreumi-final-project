package com.estsoft.finalproject.content.model.dto;

import lombok.Builder;

@Builder
public record StockItem(String isin, String name, Long price, String baseDate,
                        Double fluctuationRate) {

}
