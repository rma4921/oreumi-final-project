package com.estsoft.finalproject.mypage.service;

import com.estsoft.finalproject.Post.repository.ScrapPostRepository;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleDetailResponseDto;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleResponseDto;
import com.estsoft.finalproject.mypage.repository.ScrappedArticleRepository;
import com.estsoft.finalproject.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ScrappedArticleService {

    private final ScrappedArticleRepository scrappedArticleRepository;
    private final ScrapPostRepository scrapPostRepository;

    public Page<ScrappedArticleResponseDto> getScrappedArticlesByUser(Users user,
        Pageable pageable) {
        return scrappedArticleRepository.findAllByUserOrderByScrapDateDesc(user, pageable)
            .map(scrappedArticle -> scrappedArticle.toDto(
                scrappedArticle.getScrapId(),
                scrappedArticle.getTitle(),
                scrappedArticle.getTopic(),
                scrappedArticle.getScrapDate()
            ));
    }

    public Page<ScrappedArticleResponseDto> getScrappedArticlesByUserAndTitle(Users user,
        String keyword, Pageable pageable) {
        return scrappedArticleRepository.findByUserAndTitleContainingOrderByScrapDateDesc(user,
                keyword, pageable)
            .map(scrappedArticle -> scrappedArticle.toDto(
                scrappedArticle.getScrapId(),
                scrappedArticle.getTitle(),
                scrappedArticle.getTopic(),
                scrappedArticle.getScrapDate()
            ));
    }

    public ScrappedArticleDetailResponseDto getScrappedArticleDetail(Long scrapId) {
        ScrappedArticle article = scrappedArticleRepository.findById(scrapId)
            .orElseThrow(() -> new IllegalArgumentException("해당 스크랩이 없습니다. scrapId: " + scrapId));
        boolean isShared = scrapPostRepository.existsByScrappedArticle_ScrapId(scrapId);
        Long postId = 0L;

        if (isShared) {
            postId = scrapPostRepository.findByScrappedArticle(article)
                .getPostId();
        }

        return ScrappedArticleDetailResponseDto.builder()
            .scrapId(article.getScrapId())
            .postId(postId)
            .title(article.getTitle())
            .topic(article.getTopic())
            .scrapDate(article.getScrapDate())
            .description(article.getDescription())
            .link(article.getLink())
            .pubDate(article.getPubDate())
            .isShared(isShared)
            .build();
    }

    public void deleteScrappedArticle(Long scrapId) {
        scrappedArticleRepository.deleteById(scrapId);
    }

}
