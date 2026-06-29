CREATE TABLE budgets (
    id          BIGSERIAL     PRIMARY KEY,
    user_id     BIGINT        NOT NULL REFERENCES users(id),
    category_id BIGINT        NOT NULL REFERENCES categories(id),
    amount      NUMERIC(15,2) NOT NULL,
    month       SMALLINT      NOT NULL,
    year        SMALLINT      NOT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_budget_user_category_month_year UNIQUE (user_id, category_id, month, year)
);
