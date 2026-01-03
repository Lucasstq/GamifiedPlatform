-- Adiciona campos para confirmação de email
ALTER TABLE tb_user
ADD COLUMN email_verified BOOLEAN DEFAULT FALSE,
ADD COLUMN email_verification_token VARCHAR(255),
ADD COLUMN email_verification_token_expires_at TIMESTAMP;

-- Cria índice para busca rápida por token
CREATE INDEX idx_email_verification_token ON tb_user(email_verification_token);

