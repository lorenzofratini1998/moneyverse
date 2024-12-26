CREATE TABLE BUDGETS
(
    BUDGET_ID          UUID PRIMARY KEY,
    USERNAME           VARCHAR(64)  NOT NULL,
    BUDGET_NAME        VARCHAR(255) NOT NULL,
    BUDGET_DESCRIPTION TEXT,
    BUDGET_LIMIT       DECIMAL(18, 2),
    AMOUNT             DECIMAL(18, 2) DEFAULT 0.0,
    CURRENCY VARCHAR(3) NOT NULL,
    CREATED_BY         VARCHAR(255) NOT NULL,
    CREATED_AT         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    UPDATED_BY         VARCHAR(255),
    UPDATED_AT         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT UNIQUE_BUDGET_NAME_PER_USER UNIQUE (USERNAME, BUDGET_NAME)
);

COMMENT
    ON TABLE BUDGETS IS 'This table contains the information about the budgets of a user.';
COMMENT
    ON COLUMN BUDGETS.BUDGET_ID IS 'Unique self-generated id for the budget. [Primary Key]';
COMMENT
    ON COLUMN BUDGETS.USERNAME IS 'User who owns the budgets.';
COMMENT
    ON COLUMN BUDGETS.BUDGET_NAME IS 'Name of the budget.';
COMMENT
    ON COLUMN BUDGETS.BUDGET_DESCRIPTION IS 'Description of the budget.';
COMMENT
    ON COLUMN BUDGETS.BUDGET_LIMIT IS 'Limit of the budget.';
COMMENT
    ON COLUMN BUDGETS.AMOUNT IS 'Current amount of the budget. Defaults to 0.';
COMMENT
    ON COLUMN BUDGETS.CREATED_BY IS 'The username that creates the budget.';
COMMENT
    ON COLUMN BUDGETS.CREATED_AT IS 'Timestamp when the budget was created. Defaults to the current timestamp.';
COMMENT
    ON COLUMN BUDGETS.UPDATED_BY IS 'The username that updates the budget.';
COMMENT
    ON COLUMN BUDGETS.UPDATED_AT IS 'Timestamp when the budget was last updated. Defaults to the current timestamp.';


CREATE TABLE DEFAULT_BUDGET_TEMPLATES
(
    DEFAULT_BUDGET_TEMPLATE_ID UUID PRIMARY KEY,
    NAME                       VARCHAR(255) NOT NULL,
    DESCRIPTION                VARCHAR(255),
    CONSTRAINT UNIQUE_BUDGET_TEMPLATE_NAME UNIQUE (NAME)
);