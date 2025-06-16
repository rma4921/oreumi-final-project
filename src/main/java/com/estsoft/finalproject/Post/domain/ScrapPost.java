package com.estsoft.finalproject.Post.domain;

import com.estsoft.finalproject.Post.dto.ScrapPostResponseDto;
import com.estsoft.finalproject.mypage.domain.ScrappedArticle;
import com.estsoft.finalproject.user.domain.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "scrap_post")
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
public class ScrapPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrap_id", nullable = false)
    private ScrappedArticle scrappedArticle;

    @Column(name = "post_date", nullable = false)
    private LocalDateTime postDate;

    public ScrapPost(ScrappedArticle scrappedArticle) {
        this.scrappedArticle = scrappedArticle;
        this.postDate = LocalDateTime.now();
    }

    public ScrapPostResponseDto toDto() {
        Users user = this.scrappedArticle.getUser();

        return ScrapPostResponseDto.builder()
            .postId(postId)
            .nickname(user.getNickname())
            .title(scrappedArticle.getTitle())
            .topic(scrappedArticle.getTopic())
            .postDate(postDate)
            .build();
    }
}
