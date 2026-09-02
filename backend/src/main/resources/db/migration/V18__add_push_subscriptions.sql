CREATE TABLE push_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    endpoint VARCHAR(2000) NOT NULL,
    p256dh VARCHAR(200) NOT NULL,
    auth VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_push_subscriptions_user_endpoint UNIQUE (user_id, endpoint)
);
