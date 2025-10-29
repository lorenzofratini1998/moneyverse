ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_subscription_id_ref_subscriptions
        FOREIGN KEY (subscription_id) REFERENCES subscriptions (subscription_id);