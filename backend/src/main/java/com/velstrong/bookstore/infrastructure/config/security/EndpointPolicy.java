package com.velstrong.bookstore.infrastructure.config.security;

import org.springframework.http.HttpMethod;

import java.util.Locale;

public record EndpointPolicy(String method, String path, String permission) {

    public EndpointPolicy {
        method = normalizeMethod(method);
        path = normalizeText(path);
        permission = normalizeText(permission);
    }

    public static EndpointPolicy withoutPermission(String method, String path) {
        return new EndpointPolicy(method, path, null);
    }

    public HttpMethod httpMethod() {
        return HttpMethod.valueOf(method);
    }

    public String routeKey() {
        return method + " " + path;
    }

    private static String normalizeMethod(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }

    private static String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? trimmed : trimmed;
    }
}
