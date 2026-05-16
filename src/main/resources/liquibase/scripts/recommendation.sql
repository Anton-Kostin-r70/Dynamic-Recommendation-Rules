-- liquibase formatted sql

-- changeset author usr-1:1
CREATE TABLE IF NOT EXISTS rule (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rule_product_id ON rule (product_id);

-- changeset author usr-1:2
CREATE TABLE IF NOT EXISTS query (
    id UUID PRIMARY KEY,
    rule_id UUID NOT NULL REFERENCES rule(id) ON DELETE CASCADE,
    query_type VARCHAR(255) NOT NULL,
    negate BOOLEAN NOT NULL
);

CREATE INDEX idx_query_rule_id ON query (rule_id);

-- changeset author usr-1:3
CREATE TABLE IF NOT EXISTS query_argument (
    query_id UUID NOT NULL REFERENCES query(id) ON DELETE CASCADE,
    argument VARCHAR(255) NOT NULL
);

CREATE INDEX idx_query_argument_id ON query_argument(query_id);

-- changeset author usr-1:4
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE INDEX idx_users_name ON users(name);

-- changeset author usr-1:5
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_type VARCHAR(50) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_product_type ON transactions(product_type);
CREATE INDEX idx_transactions_transaction_type ON transactions(transaction_type);
CREATE INDEX idx_transactions_user_product ON transactions(user_id, product_type);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);

-- changeset author usr-1:6
ALTER TABLE transactions
ADD CONSTRAINT fk_transactions_user
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT;
