package com.estsoft.finalproject.Post.service;

import com.estsoft.finalproject.Post.domain.ScrapPost;
import com.estsoft.finalproject.Post.dto.ScrapPostResponseDto;
import com.estsoft.finalproject.Post.repository.ScrapPostRepository;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.mypage.repository.ScrappedArticleRepository;
import com.estsoft.finalproject.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapPostService {

    private final ScrapPostRepository scrapPostRepository;
    private final ScrappedArticleRepository scrappedArticleRepository;

    public void savePost(Long scrapId, Users user) {
        if (scrapPostRepository.existsByScrappedArticle_ScrapId(scrapId)) {
            throw new IllegalStateException("이미 게시된 스크랩 기사입니다.");
        }

        ScrappedArticle scrappedArticle = scrappedArticleRepository.findById(scrapId)
            .orElseThrow(() -> new IllegalArgumentException("해당 스크랩 기사가 존재하지 않습니다."));

        if (!scrappedArticle.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("본인의 스크랩 기사만 공유할 수 있습니다.");
        }

        scrapPostRepository.save(new ScrapPost(scrappedArticle));
    }

    public Page<ScrapPostResponseDto> getAllPosts(Pageable pageable) {
        return scrapPostRepository.findAll(pageable)
            .map(ScrapPost::toDto);
    }

    public Page<ScrapPostResponseDto> getAllPostsByTitle(String keyword, Pageable pageable) {
        return scrapPostRepository.findByScrappedArticle_TitleContaining(keyword, pageable)
            .map(ScrapPost::toDto);
    }

}
