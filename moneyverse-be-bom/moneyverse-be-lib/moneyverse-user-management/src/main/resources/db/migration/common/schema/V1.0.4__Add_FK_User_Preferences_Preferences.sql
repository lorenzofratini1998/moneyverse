ALTER TABLE user_preferences
    ADD CONSTRAINT fk_user_preferences_preference_id_ref_preferences
        FOREIGN KEY (preference_id) REFERENCES preferences (preference_id);