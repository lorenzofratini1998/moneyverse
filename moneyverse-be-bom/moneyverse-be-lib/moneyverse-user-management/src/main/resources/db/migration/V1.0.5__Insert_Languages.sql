INSERT INTO languages(language_id, iso_code, country, icon, is_default, enabled)
VALUES (gen_random_uuid(), 'en_US', 'English', 'usa', TRUE, TRUE),
       (gen_random_uuid(), 'es_ES', 'Spanish', 'spain', FALSE, TRUE),
       (gen_random_uuid(), 'de_DE', 'German', 'germany', FALSE, TRUE),
       (gen_random_uuid(), 'it_IT', 'Italian', 'italy', FALSE, TRUE)