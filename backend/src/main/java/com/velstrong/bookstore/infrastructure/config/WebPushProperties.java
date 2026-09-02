package com.velstrong.bookstore.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.web-push")
public record WebPushProperties(boolean enabled, String publicKey, String privateKey, String subject) { }
