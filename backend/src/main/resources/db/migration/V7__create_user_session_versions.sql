CREATE TABLE user_session_versions (
    user_id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_user_session_versions_version_nonnegative CHECK (version >= 0)
);
