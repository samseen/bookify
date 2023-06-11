CREATE TABLE m_user
(
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(60),
    password    TEXT,
    role        VARCHAR(60) DEFAULT 'USER',
    active      BOOLEAN     DEFAULT TRUE,
    merchant_id BIGINT      DEFAULT NULL,
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX index_m_user_email ON m_user (email);
CREATE INDEX index_m_user_merchant_id ON m_user (merchant_id);
CREATE INDEX index_m_user_role ON m_user(role);
CREATE INDEX index_m_user_created_at ON m_user(created_at);

CREATE TABLE merchant
(
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(120),
    rc_number   VARCHAR(60) UNIQUE,
    code        VARCHAR(4) NOT NULL,
    account_id  VARCHAR(120) UNIQUE,
    created_at  TIMESTAMP DEFAULT CURRENT_DATE
);
CREATE INDEX index_merchant_rcnumber ON merchant (rc_number);
CREATE INDEX index_merchant_account_id ON merchant (account_id);
CREATE INDEX index_merchant_code ON merchant (code);
CREATE INDEX index_merchant_code_account_id ON merchant (code, account_id);
CREATE INDEX index_merchant_created_at ON merchant (created_at);