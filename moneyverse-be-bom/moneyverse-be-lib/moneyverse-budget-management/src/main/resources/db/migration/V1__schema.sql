CREATE TABLE budgets
(
    budget_id          UUID         NOT NULL,
    user_id UUID NOT NULL,
    budget_name        VARCHAR(255) NOT NULL,
    budget_description VARCHAR(255),
    budget_limit       DECIMAL(18, 2),
    amount  DECIMAL(18, 2) DEFAULT 0.0,
    currency           VARCHAR(3)   NOT NULL,
    created_by         VARCHAR(255) NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by         VARCHAR(255),
    updated_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_budgets PRIMARY KEY (budget_id)
);

CREATE TABLE default_budget_templates
(
    default_budget_template_id UUID         NOT NULL,
    name                       VARCHAR(255) NOT NULL,
    description                VARCHAR(255),
    CONSTRAINT pk_default_budget_templates PRIMARY KEY (default_budget_template_id)
);

ALTER TABLE budgets
    ADD CONSTRAINT uc_520ab3469ae1482057b4c5fb4 UNIQUE (user_id, budget_name);

ALTER TABLE default_budget_templates
    ADD CONSTRAINT uc_default_budget_templates_name UNIQUE (name);