package com.estsoft.finalproject.mypage.service;

import com.estsoft.finalproject.mypage.dto.RelatedStockResponseDTO;
import com.estsoft.finalproject.mypage.repository.RelatedStockRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelatedStockService {

    private final RelatedStockRepository relatedStockRepository;

    public List<RelatedStockResponseDTO> findByScrapId(Long scrapId) {
        return relatedStockRepository.findByScrappedArticle_ScrapId(scrapId)
            .stream()
            .map(stock ->
                new RelatedStockResponseDTO(stock.getName()))
            .toList();
    }
}
