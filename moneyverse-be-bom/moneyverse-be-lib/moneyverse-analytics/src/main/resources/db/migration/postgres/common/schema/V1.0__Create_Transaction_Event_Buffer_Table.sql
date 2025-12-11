CREATE TABLE transaction_events_buffer
(
    event_id                UUID         NOT NULL,
    event_type              VARCHAR(255) NOT NULL,
    user_id                 UUID         NOT NULL,
    transaction_id          UUID         NOT NULL,
    original_transaction_id UUID,
    account_id              UUID         NOT NULL,
    category_id             UUID,
    budget_id               UUID,
    amount                  DECIMAL      NOT NULL,
    normalized_amount       DECIMAL      NOT NULL,
    currency                VARCHAR(255) NOT NULL,
    date                    DATE         NOT NULL,
    event_timestamp         TIMESTAMP    NOT NULL,
    state                   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_transaction_events_buffer PRIMARY KEY (event_id)
);