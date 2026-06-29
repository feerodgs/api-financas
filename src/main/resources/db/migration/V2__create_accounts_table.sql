CREATE TABLE accounts (
    id         BIGSERIAL     PRIMARY KEY,
    user_id    BIGINT        NOT NULL REFERENCES users(id),
    name       VARCHAR(100)  NOT NULL,
    type       VARCHAR(30)   NOT NULL,
    balance    NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP     NOT NULL DEFAULT NOW()
);
