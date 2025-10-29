CREATE TABLE transactions_tags
(
    tag_id         UUID NOT NULL,
    transaction_id UUID NOT NULL,
    CONSTRAINT pk_transactions_tags PRIMARY KEY (tag_id, transaction_id)
);

ALTER TABLE transactions_tags
    ADD CONSTRAINT fk_transactions_tags_tag_id_ref_tags
        FOREIGN KEY (tag_id) REFERENCES tags (tag_id);

ALTER TABLE transactions_tags
    ADD CONSTRAINT fk_transactions_tags_transaction_id_ref_transactions
        FOREIGN KEY (transaction_id) REFERENCES transactions (transaction_id);