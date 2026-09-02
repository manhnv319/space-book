package com.velstrong.bookstore.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/** Local fallback; production Nginx serves the same directory before requests reach Spring. */
@Configuration
public class MediaResourceWebConfig implements WebMvcConfigurer {
    private final MediaStorageProperties media;

    public MediaResourceWebConfig(MediaStorageProperties media) {
        this.media = media;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Path.of(media.storagePath()).toUri().toString();
        registry.addResourceHandler("/media/**").addResourceLocations(location.endsWith("/") ? location : location + "/");
    }
}
