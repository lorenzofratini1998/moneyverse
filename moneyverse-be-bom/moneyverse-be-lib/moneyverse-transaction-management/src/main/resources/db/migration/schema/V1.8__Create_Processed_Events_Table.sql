CREATE TABLE processed_events
(
    event_id     UUID         NOT NULL,
    topic        VARCHAR(255) NOT NULL,
    payload      JSON         NOT NULL,
    event_type   VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_processed_events PRIMARY KEY (event_id)
);