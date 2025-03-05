CREATE TABLE subscriptions
(
    subscription_id     UUID           NOT NULL,
    user_id             UUID           NOT NULL,
    account_id          UUID           NOT NULL,
    category_id         UUID,
    amount              DECIMAL(18, 2) NOT NULL,
    total_amount        DECIMAL(18, 2) NOT NULL     DEFAULT 0,
    currency            VARCHAR(255)   NOT NULL,
    subscription_name   VARCHAR(255)   NOT NULL,
    recurrence_rule     VARCHAR(255)   NOT NULL,
    start_date          DATE           NOT NULL,
    end_date            DATE,
    next_execution_date DATE,
    is_active           BOOLEAN        NOT NULL     DEFAULT TRUE,
    created_by          VARCHAR(255)   NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(255),
    updated_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_subscriptions PRIMARY KEY (subscription_id)
);

