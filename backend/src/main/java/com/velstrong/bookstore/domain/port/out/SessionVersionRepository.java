package com.velstrong.bookstore.domain.port.out;

public interface SessionVersionRepository {
    long currentVersion(Long userId);
    long incrementVersion(Long userId);
}
