package com.estsoft.finalproject.mypage.domain;

import com.estsoft.finalproject.mypage.dto.ScrappedArticleResponseDto;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// 진우님이랑 맞춰봐야 함.
@Getter
@Entity
@Table(name = "scrapped_article")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ScrappedArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scrapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime scrapDate;

    @Column(length = 300, nullable = false)
    private String title;

    @Column(length = 300, nullable = false)
    private String link;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "pub_date", nullable = false)
    private LocalDateTime pubDate;

    @Column(length = 100, nullable = false)
    private String topic;

    public ScrappedArticleResponseDto toDto(Long scrapId, String title, String topic,
        LocalDateTime scrapDate) {
        return new ScrappedArticleResponseDto(scrapId, title, topic, scrapDate);
    }

    public void updateScrapId(Long scrapId) {
        this.scrapId = scrapId;
    }

}
