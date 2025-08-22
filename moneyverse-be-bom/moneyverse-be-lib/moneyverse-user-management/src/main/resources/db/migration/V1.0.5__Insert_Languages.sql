INSERT INTO languages(language_id, iso_code, locale, country, icon, is_default, enabled)
VALUES (gen_random_uuid(), 'en', 'en-US', 'English', 'usa', TRUE, TRUE),
       (gen_random_uuid(), 'es', 'es-ES', 'Spanish', 'spain', FALSE, TRUE),
       (gen_random_uuid(), 'de', 'de-DE', 'German', 'germany', FALSE, TRUE),
       (gen_random_uuid(), 'it', 'it-IT', 'Italian', 'italy', FALSE, TRUE)