package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.User;

import java.util.List;

/**
 * F20: driven port for JWT operations. Implementations live under
 * {@code infrastructure.adapter.out.external}.
 */
public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    Long extractUserId(String token);
    List<String> extractRoles(String token);
    List<String> extractPermissions(String token);
    boolean isTokenValid(String token);
    long extractSessionVersion(String token);
    long getAccessTokenExpiry();
    long getRefreshTokenExpiry();
}
