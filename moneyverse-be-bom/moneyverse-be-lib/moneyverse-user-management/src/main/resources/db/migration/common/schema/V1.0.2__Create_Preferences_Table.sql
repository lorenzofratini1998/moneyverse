CREATE TABLE preferences
(
    preference_id UUID                  NOT NULL,
    name          VARCHAR(255)          NOT NULL,
    mandatory     BOOLEAN DEFAULT FALSE NOT NULL,
    updatable     BOOLEAN DEFAULT TRUE  NOT NULL,
    default_value VARCHAR(255),
    CONSTRAINT pk_preferences PRIMARY KEY (preference_id)
);

ALTER TABLE preferences
    ADD CONSTRAINT uc_preferences_name UNIQUE (name);