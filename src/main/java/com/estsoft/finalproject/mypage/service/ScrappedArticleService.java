package com.estsoft.finalproject.mypage.service;

import com.estsoft.finalproject.Post.repository.ScrapPostRepository;
import com.estsoft.finalproject.content.model.dto.ResponseDto;
import com.estsoft.finalproject.mypage.domain.RelatedStock;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleDetailResponseDto;
import com.estsoft.finalproject.mypage.dto.ScrappedArticleResponseDto;
import com.estsoft.finalproject.mypage.repository.RelatedStockRepository;
import com.estsoft.finalproject.mypage.repository.ScrappedArticleRepository;
import com.estsoft.finalproject.user.domain.Users;
import com.estsoft.finalproject.user.dto.CustomUsersDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class ScrappedArticleService {

    private final ScrappedArticleRepository scrappedArticleRepository;
    private final ScrapPostRepository scrapPostRepository;
    private final RelatedStockRepository relatedStockRepository;

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

/* 
 *                     title: headline,
                    content: content,
                    topic: topic,
                    category: category,
                    link: link,
                    relatedCompanies: relatedCompanyNames
*/

    public ResponseDto<ScrappedArticleResponseDto> scrapAnArticle(String scrapData, CustomUsersDetails userInfo) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jn = mapper.readTree(scrapData);
            String title = Optional.ofNullable(jn.get("title")).map(JsonNode::asText).orElse("null");
            String topic = Optional.ofNullable(jn.get("topic")).map(JsonNode::asText).orElse("null");
            String content = Optional.ofNullable(jn.get("content")).map(JsonNode::asText).orElse("null");
            String link = Optional.ofNullable(jn.get("link")).map(JsonNode::asText).orElse("null");
            Optional<JsonNode> companies = Optional.ofNullable(jn.get("relatedCompanies"));
            if (title.equalsIgnoreCase("null") || topic.equalsIgnoreCase("null") || 
                content.equalsIgnoreCase("null") || link.equalsIgnoreCase("null")) {
                ScrappedArticleResponseDto dto = new ScrappedArticleResponseDto(-1L, title, topic, LocalDateTime.now());
                return ResponseDto.builder(dto).message("A NULL value was found in the JSON request data").responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            Users user = userInfo.getUsers();
            ScrappedArticle scrapped = scrappedArticleRepository.save(ScrappedArticle.builder()
                .title(title)
                .description(content)
                .topic(topic)
                .link(link)
                .scrapDate(LocalDateTime.now()).pubDate(LocalDateTime.now()).user(user).build());
            companies.ifPresent(x -> x.forEach(y -> relatedStockRepository.save(RelatedStock.builder().name(y.asText()).scrappedArticle(scrapped).build())));

            ScrappedArticleResponseDto dto = new ScrappedArticleResponseDto(scrapped.getScrapId(), title, topic, scrapped.getScrapDate());
            return ResponseDto.builder(dto).message("Saved").responseCode(HttpStatus.OK).build();
        } catch (JsonProcessingException jex) {
                ScrappedArticleResponseDto dto = new ScrappedArticleResponseDto(-1L, "error", "error", LocalDateTime.now());
                return ResponseDto.builder(dto).message("An error occurred while parsing JSON data").responseCode(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
