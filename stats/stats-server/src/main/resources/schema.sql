DROP TABLE IF EXISTS stats;

CREATE TABLE IF NOT EXISTS stats (
                                     id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
                                     app VARCHAR(255) NOT NULL,
                                     uri VARCHAR(512) NOT NULL,
                                     ip VARCHAR(50) NOT NULL,
                                     time_of_hit TIMESTAMP WITHOUT TIME ZONE NOT NULL
);