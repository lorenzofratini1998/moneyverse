INSERT INTO preferences(preference_id, name, mandatory, updatable)
VALUES (gen_random_uuid(), 'LANGUAGE', true, true),
       (gen_random_uuid(), 'CURRENCY', true, false),
       (gen_random_uuid(), 'DATE_FORMAT', true, true)