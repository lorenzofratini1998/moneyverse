CREATE TABLE languages
(
    language_id UUID                  NOT NULL,
    iso_code    VARCHAR(3)            NOT NULL,
    country     VARCHAR(50)           NOT NULL,
    is_default  BOOLEAN DEFAULT FALSE NOT NULL,
    enabled     BOOLEAN DEFAULT TRUE  NOT NULL,
    CONSTRAINT pk_languages PRIMARY KEY (language_id)
);

CREATE TABLE preferences
(
    preference_id    UUID         NOT NULL,
    created_by       VARCHAR(255) NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(255),
    updated_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_id          UUID         NOT NULL,
    preference_key   VARCHAR(255) NOT NULL,
    preference_value VARCHAR(255) NOT NULL,
    updatable        BOOLEAN                     DEFAULT TRUE,
    CONSTRAINT pk_preferences PRIMARY KEY (preference_id)
);

ALTER TABLE preferences
    ADD CONSTRAINT uc_7a50533eab7af66ef039a83b1 UNIQUE (user_id, preference_key);