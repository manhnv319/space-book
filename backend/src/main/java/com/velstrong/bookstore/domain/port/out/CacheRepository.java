package com.velstrong.bookstore.domain.port.out;

import java.time.Duration;
import java.util.Optional;

public interface CacheRepository {
    void put(String key, Object value, Duration ttl);
    <T> Optional<T> get(String key, Class<T> type);
    void delete(String key);
    boolean exists(String key);
}
