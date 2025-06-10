package com.estsoft.finalproject.content.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@AllArgsConstructor
@Builder
public class ScrappedNewsItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    //@ManyToOne(cascade = CascadeType.ALL)
    //@Column(name = "users")
    //private User user;
    @Column(name = "headline", columnDefinition = "VARCHAR(200)")
    private String headline;
    @Column(name = "link", columnDefinition = "VARCHAR(200)")
    private String link;
    @Column(name = "content", columnDefinition = "LONGTEXT")
    @Lob
    private String content;
    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;
}