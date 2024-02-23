CREATE TABLE IF NOT EXISTS todo
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    task      VARCHAR(255),
    completed BOOLEAN DEFAULT FALSE,
    CONSTRAINT pk_todo PRIMARY KEY (id)
);