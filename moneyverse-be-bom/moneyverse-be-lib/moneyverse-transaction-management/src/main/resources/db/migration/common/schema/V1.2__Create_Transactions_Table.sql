CREATE TABLE transactions
(
    transaction_id    UUID           NOT NULL,
    user_id           UUID           NOT NULL,
    account_id        UUID           NOT NULL,
    category_id       UUID,
    budget_id         UUID,
    date              DATE           NOT NULL,
    description       VARCHAR(255)   NOT NULL,
    amount            DECIMAL(18, 2) NOT NULL,
    normalized_amount DECIMAL(18, 2) NOT NULL,
    currency          VARCHAR(3)     NOT NULL,
    transfer_id       UUID,
    subscription_id   UUID,
    created_by        VARCHAR(255)   NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by        VARCHAR(255),
    updated_at        TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_transactions PRIMARY KEY (transaction_id)
);
