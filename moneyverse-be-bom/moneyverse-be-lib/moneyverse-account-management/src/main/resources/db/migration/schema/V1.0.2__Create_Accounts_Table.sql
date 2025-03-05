CREATE TABLE accounts
(
    account_id          UUID                                      NOT NULL,
    user_id             UUID                                      NOT NULL,
    account_name        VARCHAR(255)                              NOT NULL,
    balance             DECIMAL(18, 2)              DEFAULT 0.0   NOT NULL,
    balance_target      DECIMAL(18, 2),
    account_description VARCHAR(255),
    is_default          BOOLEAN                     DEFAULT FALSE NOT NULL,
    currency            VARCHAR(3)                                NOT NULL,
    account_category    BIGINT                                    NOT NULL,
    created_by          VARCHAR(255)                              NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(255),
    updated_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_accounts PRIMARY KEY (account_id)
);

ALTER TABLE accounts
    ADD CONSTRAINT FK_ACCOUNTS_ON_ACCOUNT_CATEGORY FOREIGN KEY (account_category) REFERENCES account_categories (account_category_id) ON DELETE RESTRICT;