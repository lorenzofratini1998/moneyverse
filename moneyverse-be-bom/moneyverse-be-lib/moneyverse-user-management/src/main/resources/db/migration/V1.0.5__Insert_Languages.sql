INSERT INTO languages(language_id, iso_code, country, is_default, enabled)
VALUES (gen_random_uuid(), 'ENG', 'English', TRUE, TRUE),
       (gen_random_uuid(), 'SPA', 'Spanish', FALSE, TRUE),
       (gen_random_uuid(), 'DEU', 'German', FALSE, TRUE),
       (gen_random_uuid(), 'ITA', 'Italian', FALSE, TRUE)