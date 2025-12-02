CREATE TABLE default_categories
(
    default_category_id UUID         NOT NULL,
    name                VARCHAR(255) NOT NULL,
    description         VARCHAR(255),
    color               VARCHAR(255),
    icon                VARCHAR(255),
    CONSTRAINT pk_default_categories PRIMARY KEY (default_category_id)
);

ALTER TABLE default_categories
    ADD CONSTRAINT uc_default_categories_name UNIQUE (name);