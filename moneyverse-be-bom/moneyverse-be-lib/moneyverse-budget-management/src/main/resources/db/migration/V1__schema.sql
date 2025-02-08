CREATE TABLE budgets
(
    budget_id    UUID           NOT NULL,
    created_by   VARCHAR(255)   NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by   VARCHAR(255),
    updated_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    category_id  UUID           NOT NULL,
    start_date   date           NOT NULL,
    end_date     date           NOT NULL,
    amount DECIMAL(18, 2) NOT NULL DEFAULT 0.0,
    budget_limit DECIMAL(18, 2) NOT NULL,
    currency     VARCHAR(3)     NOT NULL,
    CONSTRAINT pk_budgets PRIMARY KEY (budget_id)
);

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
    parent_id UUID,
    CONSTRAINT pk_categories PRIMARY KEY (category_id)
);

CREATE TABLE default_categories
(
    default_category_id UUID         NOT NULL,
    name                VARCHAR(255) NOT NULL,
    description         VARCHAR(255),
    CONSTRAINT pk_default_categories PRIMARY KEY (default_category_id)
);

ALTER TABLE categories
    ADD CONSTRAINT uc_9e5da0178316d3f370e65e13d UNIQUE (user_id, category_name);

ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_ON_PARENT FOREIGN KEY (parent_id) REFERENCES categories (category_id);

ALTER TABLE default_categories
    ADD CONSTRAINT uc_default_categories_name UNIQUE (name);

ALTER TABLE budgets
    ADD CONSTRAINT FK_BUDGETS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (category_id);