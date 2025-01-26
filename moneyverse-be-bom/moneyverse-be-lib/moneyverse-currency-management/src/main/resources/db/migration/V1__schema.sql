CREATE TABLE currencies
(
    currency_id   UUID       NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    currency_name VARCHAR(50),
    country       VARCHAR(50),
    CONSTRAINT pk_currencies PRIMARY KEY (currency_id)
);

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

ALTER TABLE currencies
    ADD CONSTRAINT uc_currencies_currency_code UNIQUE (currency_code);