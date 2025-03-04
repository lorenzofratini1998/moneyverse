CREATE TABLE currencies
(
    currency_id   UUID                  NOT NULL,
    iso_code      VARCHAR(3)            NOT NULL,
    currency_name VARCHAR(50)           NOT NULL,
    country       VARCHAR(50)           NOT NULL,
    is_default    BOOLEAN DEFAULT FALSE NOT NULL,
    is_enabled    BOOLEAN DEFAULT TRUE  NOT NULL,
    CONSTRAINT pk_currencies PRIMARY KEY (currency_id)
);

ALTER TABLE currencies
    ADD CONSTRAINT uc_currencies_iso_code UNIQUE (iso_code);