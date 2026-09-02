package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.port.out.SessionVersionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("postgres & !mongodb")
public class SessionVersionPersistenceAdapter implements SessionVersionRepository {
    private final JdbcTemplate jdbcTemplate;

    public SessionVersionPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long currentVersion(Long userId) {
        Long version = jdbcTemplate.queryForObject(
                "SELECT COALESCE((SELECT version FROM user_session_versions WHERE user_id = ?), 0)",
                Long.class, userId);
        return version == null ? 0 : version;
    }

    @Override
    public long incrementVersion(Long userId) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO user_session_versions(user_id, version) VALUES (?, 1) "
                        + "ON CONFLICT (user_id) DO UPDATE SET version = user_session_versions.version + 1 "
                        + "RETURNING version",
                Long.class, userId);
    }
}
