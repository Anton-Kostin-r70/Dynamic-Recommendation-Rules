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