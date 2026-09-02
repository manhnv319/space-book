package com.velstrong.bookstore.infrastructure.adapter.out.push;

import com.velstrong.bookstore.domain.port.out.PushConfiguration;
import com.velstrong.bookstore.infrastructure.config.WebPushProperties;
import org.springframework.stereotype.Component;

@Component
public class WebPushConfigurationAdapter implements PushConfiguration {
    private final WebPushProperties properties;
    public WebPushConfigurationAdapter(WebPushProperties properties) { this.properties = properties; }
    @Override public String publicKey() { return properties.enabled() ? properties.publicKey() : ""; }
}
