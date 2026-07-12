--liquibase formatted sql
--changeset parnas:001
CREATE TABLE users (
    id UUID PRIMARY KEY,
    wallet_address VARCHAR(42) NOT NULL UNIQUE,
    username VARCHAR(32) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_login_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE campaigns (
    id UUID PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    short_description VARCHAR(300),
    description VARCHAR(5000),
    creator_id UUID NOT NULL REFERENCES users(id),
    target_amount VARCHAR(78) NOT NULL,
    raised_amount VARCHAR(78) NOT NULL DEFAULT '0',
    status VARCHAR(30) NOT NULL,
    deadline TIMESTAMP WITH TIME ZONE NOT NULL,
    chain_id INT NOT NULL,
    contract_address VARCHAR(42),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE contributions (
    id UUID PRIMARY KEY,
    campaign_id UUID NOT NULL REFERENCES campaigns(id),
    contributor_user_id UUID REFERENCES users(id),
    contributor_wallet VARCHAR(42) NOT NULL,
    amount VARCHAR(78) NOT NULL,
    status VARCHAR(20) NOT NULL,
    transaction_hash VARCHAR(66),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    confirmed_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE payouts (
    id UUID PRIMARY KEY,
    campaign_id UUID NOT NULL REFERENCES campaigns(id),
    profit_amount VARCHAR(78) NOT NULL,
    status VARCHAR(20) NOT NULL,
    transaction_hash VARCHAR(66),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    confirmed_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE payout_distributions (
    id UUID PRIMARY KEY,
    payout_id UUID NOT NULL REFERENCES payouts(id),
    wallet_address VARCHAR(42) NOT NULL,
    contribution_amount VARCHAR(78) NOT NULL,
    share_percent DECIMAL(21,6) NOT NULL,
    payout_amount VARCHAR(78) NOT NULL,
    transfer_status VARCHAR(20) NOT NULL,
    transaction_hash VARCHAR(66)
);

CREATE TABLE blockchain_transactions (
    hash VARCHAR(66) PRIMARY KEY,
    network VARCHAR(20) NOT NULL,
    chain_id INT NOT NULL,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    from_address VARCHAR(42) NOT NULL,
    to_address VARCHAR(42),
    value_raw VARCHAR(78),
    block_number BIGINT,
    block_hash VARCHAR(66),
    confirmations INT NOT NULL DEFAULT 0,
    required_confirmations INT NOT NULL DEFAULT 12,
    gas_used VARCHAR(78),
    revert_reason VARCHAR(500),
    explorer_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    confirmed_at TIMESTAMP WITH TIME ZONE
);
