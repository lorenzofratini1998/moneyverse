CREATE TABLE exchange_rates
(
    exchange_rate_id UUID                     NOT NULL,
    date             date                     NOT NULL,
    currency_from    VARCHAR(3) DEFAULT 'EUR' NOT NULL,
    currency_to      VARCHAR(3)               NOT NULL,
    rate             DECIMAL                  NOT NULL,
    CONSTRAINT pk_exchange_rates PRIMARY KEY (exchange_rate_id)
);

ALTER TABLE exchange_rates
    ADD CONSTRAINT uc_cb901954895f44f4050b93199 UNIQUE (date, currency_to);