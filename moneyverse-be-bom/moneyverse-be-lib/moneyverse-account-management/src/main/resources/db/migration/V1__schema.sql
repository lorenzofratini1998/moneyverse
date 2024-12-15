CREATE TABLE ACCOUNTS
(
    ACCOUNT_ID          UUID PRIMARY KEY,
    USERNAME            VARCHAR(64)  NOT NULL,
    ACCOUNT_NAME        VARCHAR(255) NOT NULL,
    BALANCE             DECIMAL(18, 2) DEFAULT 0.0,
    BALANCE_TARGET      DECIMAL(18, 2),
    ACCOUNT_CATEGORY    VARCHAR(20)  NOT NULL,
    ACCOUNT_DESCRIPTION TEXT,
    IS_DEFAULT          BOOLEAN        DEFAULT FALSE,
    CREATED_BY          VARCHAR(255) NOT NULL,
    CREATED_AT          TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    UPDATED_BY          VARCHAR(255),
    UPDATED_AT          TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

COMMENT
ON TABLE ACCOUNTS IS 'This table contains the information about the financial accounts held by users.';
COMMENT
ON COLUMN ACCOUNTS.ACCOUNT_ID IS 'Unique self-generated id for the account. [Primary Key]';
COMMENT
ON COLUMN ACCOUNTS.USERNAME IS 'User who owns the accounts.';
COMMENT
ON COLUMN ACCOUNTS.ACCOUNT_NAME IS 'Name of the account.';
COMMENT
ON COLUMN ACCOUNTS.BALANCE IS 'Current balance of the account.';
COMMENT
ON COLUMN ACCOUNTS.BALANCE_TARGET IS 'Target balance for the account.';
COMMENT
ON COLUMN ACCOUNTS.ACCOUNT_CATEGORY IS 'Category of the account.';
COMMENT
ON COLUMN ACCOUNTS.ACCOUNT_DESCRIPTION IS 'Description of the account.';
COMMENT
ON COLUMN ACCOUNTS.IS_DEFAULT IS 'Indicates whether this accounts is the default account for the user or not.';
COMMENT
ON COLUMN ACCOUNTS.CREATED_BY IS 'The username that creates the account.';
COMMENT
ON COLUMN ACCOUNTS.CREATED_AT IS 'Timestamp when the account was created. Defaults to the current timestamp.';
COMMENT
ON COLUMN ACCOUNTS.UPDATED_AT IS 'The username that updates the account.';
COMMENT
ON COLUMN ACCOUNTS.UPDATED_BY IS 'Timestamp when the account was last updated. Defaults to the current timestamp.';