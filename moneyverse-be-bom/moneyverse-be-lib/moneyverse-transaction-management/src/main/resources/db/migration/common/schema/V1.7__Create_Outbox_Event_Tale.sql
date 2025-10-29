CREATE TABLE outbox_events
(
    event_id       UUID                        NOT NULL,
    aggregate_type VARCHAR(255),
    topic          VARCHAR(255)                NOT NULL,
    aggregate_id   UUID                        NOT NULL,
    event_type     VARCHAR(255),
    payload        JSON                        NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    processed      BOOLEAN,
    CONSTRAINT pk_outbox_events PRIMARY KEY (event_id)
);