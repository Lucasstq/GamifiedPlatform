-- Migration: Create Refresh Tokens Table
-- Description: Tabela para armazenar refresh tokens para autenticação de longa duração
-- Date: 2026-01-07

CREATE TABLE tb_refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    user_agent VARCHAR(500),
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id)
        REFERENCES tb_user(id) ON DELETE CASCADE
);

-- Índices para melhor performance
CREATE INDEX idx_refresh_token_user_id ON tb_refresh_tokens(user_id);
CREATE INDEX idx_refresh_token_token ON tb_refresh_tokens(token);
CREATE INDEX idx_refresh_token_expiry ON tb_refresh_tokens(expiry_date);
CREATE INDEX idx_refresh_token_revoked ON tb_refresh_tokens(revoked);

-- Comentários
COMMENT ON TABLE tb_refresh_tokens IS 'Armazena refresh tokens para renovação de access tokens';
COMMENT ON COLUMN tb_refresh_tokens.token IS 'UUID único do refresh token';
COMMENT ON COLUMN tb_refresh_tokens.expiry_date IS 'Data/hora de expiração do token';
COMMENT ON COLUMN tb_refresh_tokens.revoked IS 'Se true, o token foi revogado (logout)';
COMMENT ON COLUMN tb_refresh_tokens.user_agent IS 'User-Agent do navegador/dispositivo';
COMMENT ON COLUMN tb_refresh_tokens.ip_address IS 'Endereço IP da origem do token';

