package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.book.FormatType;

import java.time.LocalDateTime;
import java.util.List;

public class Book {

    private final Long id;
    private final String isbn;
    private final String title;
    private final String description;
    private final String imageUrl;
    private final FormatType format;
    private final Long listPrice;
    private final Long rentalPriceDay;
    private final Long rentalPriceWeek;
    private final Long rentalPriceMonth;
    private final Long depositAmount;
    private final Short publishYear;
    private final String publisher;
    private final String language;
    private final Short pageCount;
    private final Boolean isActive;
    private final List<String> authors;
    private final List<String> categories;
    private final LocalDateTime createdAt;
    private final Boolean isFeatured;
    private final Boolean isBestseller;

    private Book(Long id, String isbn, String title, String description, String imageUrl,
                 FormatType format, Long listPrice, Long rentalPriceDay, Long rentalPriceWeek,
                 Long rentalPriceMonth, Long depositAmount, Short publishYear, String publisher,
                 String language, Short pageCount, Boolean isActive,
                 List<String> authors, List<String> categories,
                 LocalDateTime createdAt, Boolean isFeatured, Boolean isBestseller) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.format = format;
        this.listPrice = listPrice;
        this.rentalPriceDay = rentalPriceDay;
        this.rentalPriceWeek = rentalPriceWeek;
        this.rentalPriceMonth = rentalPriceMonth;
        this.depositAmount = depositAmount;
        this.publishYear = publishYear;
        this.publisher = publisher;
        this.language = language;
        this.pageCount = pageCount;
        this.isActive = isActive;
        this.authors = authors;
        this.categories = categories;
        this.createdAt = createdAt;
        this.isFeatured = isFeatured;
        this.isBestseller = isBestseller;
    }

    public static Book reconstitute(Long id, String isbn, String title, String description,
                                    String imageUrl, FormatType format, Long listPrice,
                                    Long rentalPriceDay, Long rentalPriceWeek, Long rentalPriceMonth,
                                    Long depositAmount, Short publishYear, String publisher,
                                    String language, Short pageCount, Boolean isActive,
                                    List<String> authors, List<String> categories,
                                    LocalDateTime createdAt, Boolean isFeatured, Boolean isBestseller) {
        return new Book(id, isbn, title, description, imageUrl, format, listPrice,
                rentalPriceDay, rentalPriceWeek, rentalPriceMonth, depositAmount,
                publishYear, publisher, language, pageCount, isActive, authors, categories,
                createdAt, isFeatured, isBestseller);
    }

    public boolean isAvailableForSale() { return Boolean.TRUE.equals(isActive) && listPrice != null && listPrice > 0; }
    public boolean isAvailableForRental() { return Boolean.TRUE.equals(isActive) && rentalPriceDay != null && rentalPriceDay > 0; }

    public Long getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public FormatType getFormat() { return format; }
    public Long getListPrice() { return listPrice; }
    public Long getRentalPriceDay() { return rentalPriceDay; }
    public Long getRentalPriceWeek() { return rentalPriceWeek; }
    public Long getRentalPriceMonth() { return rentalPriceMonth; }
    public Long getDepositAmount() { return depositAmount; }
    public Short getPublishYear() { return publishYear; }
    public String getPublisher() { return publisher; }
    public String getLanguage() { return language; }
    public Short getPageCount() { return pageCount; }
    public Boolean getIsActive() { return isActive; }
    public List<String> getAuthors() { return authors; }
    public List<String> getCategories() { return categories; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Boolean getIsFeatured() { return isFeatured; }
    public Boolean getIsBestseller() { return isBestseller; }
}
