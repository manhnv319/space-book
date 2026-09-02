package com.velstrong.bookstore.infrastructure.adapter.out.external;

import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.port.out.JwtService;
import com.velstrong.bookstore.domain.port.out.SessionVersionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtServiceImpl implements JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;
    private final SessionVersionRepository sessionVersionRepository;

    @Autowired
    public JwtServiceImpl(@Value("${app.jwt.secret}") String secret,
                          @Value("${app.jwt.access-token-expiry}") long accessTokenExpiry,
                          @Value("${app.jwt.refresh-token-expiry}") long refreshTokenExpiry,
                          SessionVersionRepository sessionVersionRepository) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
        this.sessionVersionRepository = sessionVersionRepository;
    }

    public JwtServiceImpl(String secret, long accessTokenExpiry, long refreshTokenExpiry) {
        this(secret, accessTokenExpiry, refreshTokenExpiry, new SessionVersionRepository() {
            private final ConcurrentHashMap<Long, Long> versions = new ConcurrentHashMap<>();
            public long currentVersion(Long userId) { return versions.getOrDefault(userId, 0L); }
            public long incrementVersion(Long userId) { return versions.merge(userId, 1L, Long::sum); }
        });
    }

    @Override
    public String generateAccessToken(User user) {
        return buildToken(user, accessTokenExpiry * 1000);
    }

    @Override
    public String generateRefreshToken(User user) {
        return buildToken(user, refreshTokenExpiry * 1000);
    }

    @Override
    public Long extractUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractStringListClaim(token, "roles").stream()
                .map(this::normalizeRole)
                .distinct()
                .toList();
    }

    @Override
    public List<String> extractPermissions(String token) {
        return extractStringListClaim(token, "perms");
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long extractSessionVersion(String token) {
        Claims claims = parseClaims(token);
        Object value = claims.get("sessionVersion");
        if (!(value instanceof Number number)) throw new IllegalArgumentException("Missing session version");
        return number.longValue();
    }

    @Override
    public long getAccessTokenExpiry() { return accessTokenExpiry; }

    @Override
    public long getRefreshTokenExpiry() { return refreshTokenExpiry; }

    private String buildToken(User user, long expiryMs) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("roles", normalizeRoles(user.getRoles()))
                .claim("perms", normalizePermissions(user.getScopes()))
                .claim("sessionVersion", sessionVersionRepository.currentVersion(user.getId()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(secretKey)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private List<String> extractStringListClaim(String token, String claimName) {
        Claims claims = parseClaims(token);
        Object raw = claims.get(claimName);
        if (raw instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }
        return List.of();
    }

    private List<String> normalizeRoles(List<String> roles) {
        if (roles == null) return List.of();
        Set<String> normalized = new LinkedHashSet<>();
        for (String role : roles) {
            if (role == null || role.isBlank()) continue;
            normalized.add(normalizeRole(role));
        }
        return List.copyOf(normalized);
    }

    private String normalizeRole(String role) {
        String normalized = role.trim().toUpperCase();
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring("ROLE_".length());
        }
        return normalized;
    }

    private List<String> normalizePermissions(List<String> permissions) {
        if (permissions == null) return List.of();
        return permissions.stream()
                .filter(p -> p != null && !p.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
    }
}
