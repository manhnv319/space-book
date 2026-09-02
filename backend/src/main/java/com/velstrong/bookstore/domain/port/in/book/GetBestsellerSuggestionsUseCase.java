package com.velstrong.bookstore.domain.port.in.book;

import com.velstrong.bookstore.application.response.book.BestsellerSuggestionResponse;

import java.util.List;

public interface GetBestsellerSuggestionsUseCase {
    List<BestsellerSuggestionResponse> getSuggestions(int limit, int days, String itemType);
}
