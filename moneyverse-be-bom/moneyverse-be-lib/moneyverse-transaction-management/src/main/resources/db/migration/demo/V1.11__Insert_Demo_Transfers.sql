INSERT INTO transactions (transaction_id, user_id, account_id, "date", description, amount, normalized_amount, currency, created_by, created_at, updated_by, updated_at)
VALUES
    ('68d971c8-0703-4abb-aaf4-8a375248ea91'::uuid, '${DEMO_USER_ID}'::uuid, 'a1000000-0000-0000-0000-000000000001', '2024-03-25', 'Transfer to Joint Bank Corp.', -300.00, -300.00, 'EUR', '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP),
    ('43069829-e8cd-4dca-9cf2-203cd0562c98'::uuid, '${DEMO_USER_ID}'::uuid, 'a1000000-0000-0000-0000-000000000002', '2024-03-25', 'Transfer from United Credit Union', 300.00, 300.00, 'EUR', '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP);

INSERT INTO transfers (transfer_id, transaction_from_id, transaction_to_id, "date", amount, currency, user_id, created_by, created_at, updated_by, updated_at) VALUES ('61000000-0000-0000-0000-000000000001'::uuid, '68d971c8-0703-4abb-aaf4-8a375248ea91'::uuid, '43069829-e8cd-4dca-9cf2-203cd0562c98'::uuid, '2024-03-25', 300.00, 'EUR', '${DEMO_USER_ID}'::uuid, '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP);

UPDATE TRANSACTIONS SET transfer_id = '61000000-0000-0000-0000-000000000001' WHERE TRANSACTION_ID = '68d971c8-0703-4abb-aaf4-8a375248ea91';
UPDATE TRANSACTIONS SET transfer_id = '61000000-0000-0000-0000-000000000001' WHERE TRANSACTION_ID = '43069829-e8cd-4dca-9cf2-203cd0562c98';

INSERT INTO transactions (transaction_id, user_id, account_id, "date", description, amount, normalized_amount, currency, created_by, created_at, updated_by, updated_at)
VALUES
    ('0ec0258b-1755-4814-b5ba-333d02d9568b'::uuid, '${DEMO_USER_ID}'::uuid, 'a1000000-0000-0000-0000-000000000001', '2024-06-25', 'Transfer to Joint Bank Corp.', -300.00, -300.00, 'EUR', '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP),
    ('db7ae2e2-6f65-4c12-bda7-1eabcf78674a'::uuid, '${DEMO_USER_ID}'::uuid, 'a1000000-0000-0000-0000-000000000002', '2024-06-25', 'Transfer from United Credit Union', 300.00, 300.00, 'EUR', '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP);

INSERT INTO transfers (transfer_id, transaction_from_id, transaction_to_id, "date", amount, currency, user_id, created_by, created_at, updated_by, updated_at) VALUES('61000000-0000-0000-0000-000000000002'::uuid, '0ec0258b-1755-4814-b5ba-333d02d9568b'::uuid, 'db7ae2e2-6f65-4c12-bda7-1eabcf78674a'::uuid, '2024-06-25', 300.00, 'EUR', '${DEMO_USER_ID}'::uuid, '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP);

UPDATE TRANSACTIONS SET transfer_id = '61000000-0000-0000-0000-000000000002' WHERE TRANSACTION_ID = '0ec0258b-1755-4814-b5ba-333d02d9568b';
UPDATE TRANSACTIONS SET transfer_id = '61000000-0000-0000-0000-000000000002' WHERE TRANSACTION_ID = 'db7ae2e2-6f65-4c12-bda7-1eabcf78674a';

INSERT INTO transactions (transaction_id, user_id, account_id, "date", description, amount, normalized_amount, currency, created_by, created_at, updated_by, updated_at)
VALUES
    ('c67b970f-704b-45d4-8221-e7fbfd754203'::uuid, '${DEMO_USER_ID}'::uuid, 'a1000000-0000-0000-0000-000000000001', '2024-09-25', 'Transfer to Joint Bank Corp.', -300.00, -300.00, 'EUR', '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP),
    ('0a1eecf3-d51e-47f1-993c-2e502489ec4b'::uuid, '${DEMO_USER_ID}'::uuid, 'a1000000-0000-0000-0000-000000000002', '2024-09-25', 'Transfer from United Credit Union', 300.00, 300.00, 'EUR', '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP);

INSERT INTO transfers (transfer_id, transaction_from_id, transaction_to_id, "date", amount, currency, user_id, created_by, created_at, updated_by, updated_at) VALUES('61000000-0000-0000-0000-000000000003'::uuid, 'c67b970f-704b-45d4-8221-e7fbfd754203'::uuid, '0a1eecf3-d51e-47f1-993c-2e502489ec4b'::uuid, '2024-09-25', 300.00, 'EUR', '${DEMO_USER_ID}'::uuid, '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP);

UPDATE TRANSACTIONS SET transfer_id = '61000000-0000-0000-0000-000000000003' WHERE TRANSACTION_ID = 'c67b970f-704b-45d4-8221-e7fbfd754203';
UPDATE TRANSACTIONS SET transfer_id = '61000000-0000-0000-0000-000000000003' WHERE TRANSACTION_ID = '0a1eecf3-d51e-47f1-993c-2e502489ec4b';

INSERT INTO transactions (transaction_id, user_id, account_id, "date", description, amount, normalized_amount, currency, created_by, created_at, updated_by, updated_at)
VALUES
    ('4e900792-d107-48e7-8ac1-2b7a645c82dd'::uuid, '${DEMO_USER_ID}'::uuid, 'a1000000-0000-0000-0000-000000000001', '2024-12-25', 'Transfer to Joint Bank Corp.', -300.00, -300.00, 'EUR', '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP),
    ('13b9f784-c9b6-4e68-9161-a81e7539fef1'::uuid, '${DEMO_USER_ID}'::uuid, 'a1000000-0000-0000-0000-000000000002', '2024-12-25', 'Transfer from United Credit Union', 300.00, 300.00, 'EUR', '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP);

INSERT INTO transfers (transfer_id, transaction_from_id, transaction_to_id, "date", amount, currency, user_id, created_by, created_at, updated_by, updated_at) VALUES('61000000-0000-0000-0000-000000000004'::uuid, '4e900792-d107-48e7-8ac1-2b7a645c82dd'::uuid, '13b9f784-c9b6-4e68-9161-a81e7539fef1'::uuid, '2024-12-25', 300.00, 'EUR', '${DEMO_USER_ID}'::uuid, '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP);

UPDATE TRANSACTIONS SET transfer_id = '61000000-0000-0000-0000-000000000004' WHERE TRANSACTION_ID = '4e900792-d107-48e7-8ac1-2b7a645c82dd';
UPDATE TRANSACTIONS SET transfer_id = '61000000-0000-0000-0000-000000000004' WHERE TRANSACTION_ID = '13b9f784-c9b6-4e68-9161-a81e7539fef1';

INSERT INTO transactions (transaction_id, user_id, account_id, "date", description, amount, normalized_amount, currency, created_by, created_at, updated_by, updated_at)
VALUES
    ('eef0970c-23db-4bfd-9e23-faec4ba1b280'::uuid, '${DEMO_USER_ID}'::uuid, 'a1000000-0000-0000-0000-000000000001', '2025-03-31', 'Transfer to Joint Bank Corp.', -250.00, -250.00, 'EUR', '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP),
    ('92a4d5d6-67ec-4d65-bcbe-7c52d8780253'::uuid, '${DEMO_USER_ID}'::uuid, 'a1000000-0000-0000-0000-000000000002', '2025-03-31', 'Transfer from United Credit Union', 250.00, 250.00, 'EUR', '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP);

INSERT INTO transfers (transfer_id, transaction_from_id, transaction_to_id, "date", amount, currency, user_id, created_by, created_at, updated_by, updated_at) VALUES('61000000-0000-0000-0000-000000000005'::uuid, 'eef0970c-23db-4bfd-9e23-faec4ba1b280'::uuid, '92a4d5d6-67ec-4d65-bcbe-7c52d8780253'::uuid, '2025-03-31', 250.00, 'EUR', '${DEMO_USER_ID}'::uuid, '${DEMO_EMAIL}', CURRENT_TIMESTAMP, '${DEMO_EMAIL}', CURRENT_TIMESTAMP);

UPDATE TRANSACTIONS SET transfer_id = '61000000-0000-0000-0000-000000000005' WHERE TRANSACTION_ID = 'eef0970c-23db-4bfd-9e23-faec4ba1b280';
UPDATE TRANSACTIONS SET transfer_id = '61000000-0000-0000-0000-000000000005' WHERE TRANSACTION_ID = '92a4d5d6-67ec-4d65-bcbe-7c52d8780253';
