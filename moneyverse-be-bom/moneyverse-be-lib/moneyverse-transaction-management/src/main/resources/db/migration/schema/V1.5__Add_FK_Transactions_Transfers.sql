ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_transfer_id_ref_transfers
        FOREIGN KEY (transfer_id) REFERENCES transfers (transfer_id);

ALTER TABLE transfers
    ADD CONSTRAINT fk_transfers_transaction_from_id_ref_transactions
        FOREIGN KEY (transaction_from_id) REFERENCES transactions (transaction_id);

ALTER TABLE transfers
    ADD CONSTRAINT fk_transfers_transaction_to_id_ref_transactions
        FOREIGN KEY (transaction_to_id) REFERENCES transactions (transaction_id);