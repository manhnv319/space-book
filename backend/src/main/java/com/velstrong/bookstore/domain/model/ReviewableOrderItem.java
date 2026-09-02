package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.review.ReviewSource;

public record ReviewableOrderItem(Long orderItemId, ReviewSource source) { }
