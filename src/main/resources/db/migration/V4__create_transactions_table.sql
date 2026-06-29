CREATE TABLE transactions (
    id          BIGSERIAL     PRIMARY KEY,
    user_id     BIGINT        NOT NULL REFERENCES users(id),
    account_id  BIGINT        NOT NULL REFERENCES accounts(id),
    category_id BIGINT        REFERENCES categories(id),
    description VARCHAR(255)  NOT NULL,
    amount      NUMERIC(15,2) NOT NULL,
    type        VARCHAR(10)   NOT NULL,
    date        DATE          NOT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);
