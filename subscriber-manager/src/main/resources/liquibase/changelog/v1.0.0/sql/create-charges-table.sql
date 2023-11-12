CREATE TABLE CHARGES
(
    CHARGE_ID             BIGINT,
    EXTERNAL_ID           BIGINT NOT NULL UNIQUE,
    CHARGE_DATE           DATE,
    CHARGE_TARGET_ID      BIGINT,
    SUBSCRIBER_ID         BIGINT,
    AMOUNT                REAL,
    PERIOD                VARCHAR(255),
    COMMENT               VARCHAR(255),
    PRIMARY KEY (CHARGE_ID)
);

CREATE SEQUENCE CHARGE_ID_SEQ START 1 INCREMENT 1;
