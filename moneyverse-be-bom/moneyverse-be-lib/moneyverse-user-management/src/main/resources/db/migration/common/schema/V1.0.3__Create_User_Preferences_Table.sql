CREATE TABLE user_preferences
(
    user_preference_id UUID         NOT NULL,
    user_id            UUID         NOT NULL,
    preference_id      UUID         NOT NULL,
    preference_value   VARCHAR(255) NOT NULL,
    created_by         VARCHAR(255) NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by         VARCHAR(255),
    updated_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_preferences PRIMARY KEY (user_preference_id)
);

ALTER TABLE user_preferences
    ADD CONSTRAINT uc_f586cd1e64a55550f7ca4a01e UNIQUE (user_id, preference_id);