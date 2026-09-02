package com.velstrong.bookstore.domain.port.in.payment;

import com.velstrong.bookstore.application.command.payment.ResolveUnmatchedTransferCommand;

public interface ResolveUnmatchedTransferUseCase { void resolve(ResolveUnmatchedTransferCommand command); }
