CREATE TABLE CITIES
(
    CITY_ID               BIGINT,
    NAME                  VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (CITY_ID)
);

CREATE SEQUENCE CITY_ID_SEQ START 1 INCREMENT 1;