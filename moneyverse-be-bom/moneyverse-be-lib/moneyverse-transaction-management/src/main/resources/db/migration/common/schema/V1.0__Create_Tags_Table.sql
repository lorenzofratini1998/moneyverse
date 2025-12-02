CREATE TABLE tags
(
    tag_id      UUID         NOT NULL,
    user_id     UUID         NOT NULL,
    tag_name    VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    color       VARCHAR(255),
    icon        VARCHAR(255),
    created_by  VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(255),
    updated_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_tags PRIMARY KEY (tag_id)
);

ALTER TABLE tags
    ADD CONSTRAINT uc_tag_user_id_tag_name UNIQUE (user_id, tag_name);