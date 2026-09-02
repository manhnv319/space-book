package com.velstrong.bookstore.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * F24: single source of truth for "now" so domain methods stay
 * pure (deterministic). Inject this clock into services and pass
 * {@code LocalDate.now(clock)} into the domain.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock systemClock() {
        return Clock.systemDefaultZone();
    }
}
