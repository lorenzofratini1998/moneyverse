CREATE TABLE tags
(
    tag_id      UUID         NOT NULL,
    user_id UUID NOT NULL,
    tag_name    VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    CONSTRAINT pk_tags PRIMARY KEY (tag_id)
);

CREATE SEQUENCE transactions_transaction_id_seq START 1;

CREATE TABLE transactions
(
    transaction_id UUID           NOT NULL,
    user_id     UUID NOT NULL,
    account_id     UUID           NOT NULL,
    budget_id      UUID,
    date           DATE           NOT NULL,
    description    VARCHAR(255)   NOT NULL,
    amount         DECIMAL(18, 2) NOT NULL,
    currency       VARCHAR(3)     NOT NULL,
    created_by     VARCHAR(255)   NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by     VARCHAR(255),
    updated_at     TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    transfer_id UUID,
    CONSTRAINT pk_transactions PRIMARY KEY (transaction_id)
);

CREATE TABLE transfers
(
    transfer_id         UUID           NOT NULL,
    created_by          VARCHAR(255)   NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(255),
    updated_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    transaction_from_id UUID           NOT NULL,
    transaction_to_id   UUID           NOT NULL,
    date                DATE           NOT NULL,
    amount              DECIMAL(18, 2) NOT NULL,
    currency            VARCHAR(3)     NOT NULL,
    CONSTRAINT pk_transfers PRIMARY KEY (transfer_id)
);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_TRANSFER FOREIGN KEY (transfer_id) REFERENCES transfers (transfer_id);

CREATE TABLE transactions_tags
(
    tag_id         UUID NOT NULL,
    transaction_id UUID NOT NULL,
    CONSTRAINT pk_transactions_tags PRIMARY KEY (tag_id, transaction_id)
);

ALTER TABLE tags
    ADD CONSTRAINT uc_79f55a49b4e1619821dc22dcf UNIQUE (user_id, tag_name);

ALTER TABLE transactions_tags
    ADD CONSTRAINT fk_tratag_on_tag FOREIGN KEY (tag_id) REFERENCES tags (tag_id);

ALTER TABLE transactions_tags
    ADD CONSTRAINT fk_tratag_on_transaction FOREIGN KEY (transaction_id) REFERENCES transactions (transaction_id);

ALTER TABLE transfers
    ADD CONSTRAINT uc_transfers_transaction_from UNIQUE (transaction_from_id);

ALTER TABLE transfers
    ADD CONSTRAINT uc_transfers_transaction_to UNIQUE (transaction_to_id);

ALTER TABLE transfers
    ADD CONSTRAINT FK_TRANSFERS_ON_TRANSACTION_FROM FOREIGN KEY (transaction_from_id) REFERENCES transactions (transaction_id);

ALTER TABLE transfers
    ADD CONSTRAINT FK_TRANSFERS_ON_TRANSACTION_TO FOREIGN KEY (transaction_to_id) REFERENCES transactions (transaction_id);