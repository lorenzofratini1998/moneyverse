CREATE TABLE TRANSACTION_EVENTS
(
    EVENT_ID                UUID,
    EVENT_TYPE              UInt8,
    USER_ID                 UUID,
    TRANSACTION_ID          UUID,
    ORIGINAL_TRANSACTION_ID Nullable(UUID),
    ACCOUNT_ID              UUID,
    CATEGORY_ID             Nullable(UUID),
    BUDGET_ID               Nullable(UUID),
    TAGS                    Array(UUID),
    AMOUNT                  Decimal(18, 2),
    NORMALIZED_AMOUNT       Decimal(18, 2),
    CURRENCY                String,
    DATE                    Date,
    EVENT_TIMESTAMP         DateTime
) ENGINE = ReplacingMergeTree
        PARTITION BY toYYYYMM(DATE)
        ORDER BY (USER_ID, DATE, TRANSACTION_ID)