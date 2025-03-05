CREATE TABLE categories
(
    category_id          UUID         NOT NULL,
    created_by           VARCHAR(255) NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by           VARCHAR(255),
    updated_at           TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_id              UUID         NOT NULL,
    category_name        VARCHAR(255) NOT NULL,
    category_description VARCHAR(255),
    parent_id            UUID,
    CONSTRAINT pk_categories PRIMARY KEY (category_id)
);

ALTER TABLE categories
    ADD CONSTRAINT uc_9e5da0178316d3f370e65e13d UNIQUE (user_id, category_name);

ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_ON_PARENT FOREIGN KEY (parent_id) REFERENCES categories (category_id);