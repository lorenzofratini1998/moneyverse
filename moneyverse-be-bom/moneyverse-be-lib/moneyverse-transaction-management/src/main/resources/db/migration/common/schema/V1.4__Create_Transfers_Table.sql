CREATE TABLE transfers
(
    transfer_id         UUID           NOT NULL,
    transaction_from_id UUID           NOT NULL,
    transaction_to_id   UUID           NOT NULL,
    date                DATE           NOT NULL,
    amount              DECIMAL(18, 2) NOT NULL,
    currency            VARCHAR(3)     NOT NULL,
    user_id             UUID           NOT NULL,
    created_by          VARCHAR(255)   NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(255),
    updated_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_transfers PRIMARY KEY (transfer_id)
);

ALTER TABLE transfers
    ADD CONSTRAINT uc_transfer_transaction_from UNIQUE (transaction_from_id);

ALTER TABLE transfers
    ADD CONSTRAINT uc_transfer_transaction_to UNIQUE (transaction_to_id);