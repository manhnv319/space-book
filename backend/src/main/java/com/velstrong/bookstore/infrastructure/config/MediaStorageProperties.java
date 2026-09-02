package com.velstrong.bookstore.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.media")
public record MediaStorageProperties(String storagePath, long maxBytes) {
    public MediaStorageProperties { storagePath = storagePath == null || storagePath.isBlank() ? "./uploads" : storagePath; if (maxBytes <= 0) maxBytes = 10 * 1024 * 1024; }
}
