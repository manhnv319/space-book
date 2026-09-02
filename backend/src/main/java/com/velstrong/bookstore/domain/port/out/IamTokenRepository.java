package com.velstrong.bookstore.domain.port.out;

import java.util.Optional;

public interface IamTokenRepository {
    void saveAccessToken(String userId, String token, long expirySeconds);
    void saveRefreshToken(String userId, String token, long expirySeconds);
    Optional<String> findAccessToken(String userId);
    Optional<String> findRefreshToken(String userId);
    void deleteAccessToken(String userId);
    void deleteRefreshToken(String userId);
    boolean isAccessTokenBlacklisted(String token);
    void blacklistAccessToken(String token, long expirySeconds);
}
