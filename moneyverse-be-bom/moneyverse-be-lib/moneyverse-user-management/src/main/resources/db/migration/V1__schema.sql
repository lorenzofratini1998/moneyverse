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
    preference_id UUID                  NOT NULL,
    name          VARCHAR(255)          NOT NULL,
    mandatory     BOOLEAN DEFAULT FALSE NOT NULL,
    updatable     BOOLEAN DEFAULT TRUE  NOT NULL,
    default_value VARCHAR(255),
    CONSTRAINT pk_preferences PRIMARY KEY (preference_id)
);

CREATE TABLE user_preferences
(
    user_preference_id UUID         NOT NULL,
    created_by         VARCHAR(255) NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by         VARCHAR(255),
    updated_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_id            UUID         NOT NULL,
    preference_id      UUID         NOT NULL,
    preference_value   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user_preferences PRIMARY KEY (user_preference_id)
);

ALTER TABLE user_preferences
    ADD CONSTRAINT uc_f586cd1e64a55550f7ca4a01e UNIQUE (user_id, preference_id);

ALTER TABLE preferences
    ADD CONSTRAINT uc_preferences_name UNIQUE (name);

ALTER TABLE user_preferences
    ADD CONSTRAINT FK_USER_PREFERENCES_ON_PREFERENCE FOREIGN KEY (preference_id) REFERENCES preferences (preference_id);