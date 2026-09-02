package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_reviews")
@Getter
@Setter
public class BookReviewJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long bookId;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private Long orderItemId;
    @Column(nullable = false) private String source;
    @Column(nullable = false) private Short rating;
    @Column(nullable = false, length = 2000) private String comment;
    @Column(nullable = false) private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
