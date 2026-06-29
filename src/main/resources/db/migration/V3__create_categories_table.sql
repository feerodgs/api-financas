CREATE TABLE categories (
    id         BIGSERIAL    PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES users(id),
    name       VARCHAR(100) NOT NULL,
    type       VARCHAR(10)  NOT NULL,
    color      VARCHAR(7),
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);
