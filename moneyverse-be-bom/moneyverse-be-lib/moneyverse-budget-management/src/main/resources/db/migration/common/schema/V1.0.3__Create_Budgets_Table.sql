CREATE TABLE budgets
(
    budget_id    UUID           NOT NULL,
    created_by   VARCHAR(255)   NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by   VARCHAR(255),
    updated_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    category_id  UUID           NOT NULL,
    start_date   date           NOT NULL,
    end_date     date           NOT NULL,
    amount       DECIMAL(18, 2) NOT NULL     DEFAULT 0.0,
    budget_limit DECIMAL(18, 2) NOT NULL,
    currency     VARCHAR(3)     NOT NULL,
    CONSTRAINT pk_budgets PRIMARY KEY (budget_id)
);

ALTER TABLE budgets
    ADD CONSTRAINT FK_BUDGETS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (category_id);