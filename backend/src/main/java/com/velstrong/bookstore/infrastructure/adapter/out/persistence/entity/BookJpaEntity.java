package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Getter
@Setter
public class BookJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;
    private String format;
    private Long listPrice;
    private Long rentalPriceDay;
    private Long rentalPriceWeek;
    private Long rentalPriceMonth;
    private Long depositAmount;
    private Short publishYear;
    private String publisher;
    private String language;
    private Short pageCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Boolean isFeatured;
    private Boolean isBestseller;
}
