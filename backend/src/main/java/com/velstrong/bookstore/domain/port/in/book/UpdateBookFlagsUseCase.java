package com.velstrong.bookstore.domain.port.in.book;

import com.velstrong.bookstore.application.command.book.UpdateBookFlagsCommand;

public interface UpdateBookFlagsUseCase {
    void updateFlags(UpdateBookFlagsCommand command);
}
