CREATE TABLE languages
(
    language_id UUID                  NOT NULL,
    iso_code VARCHAR(2) NOT NULL,
    locale   VARCHAR(5) NOT NULL,
    country     VARCHAR(50)           NOT NULL,
    icon     VARCHAR(10) NOT NULL,
    is_default  BOOLEAN DEFAULT FALSE NOT NULL,
    enabled     BOOLEAN DEFAULT TRUE  NOT NULL,
    CONSTRAINT pk_languages PRIMARY KEY (language_id)
);
