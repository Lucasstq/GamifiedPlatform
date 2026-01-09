-- Add OAuth2 provider fields to tb_user table
ALTER TABLE tb_user
    ADD COLUMN provider VARCHAR(20) DEFAULT 'LOCAL',
    ADD COLUMN provider_id VARCHAR(255),
    ADD COLUMN provider_email VARCHAR(255);

-- Make password nullable for OAuth2 users
ALTER TABLE tb_user ALTER COLUMN password DROP NOT NULL;

-- Add index for provider lookup
CREATE INDEX idx_user_provider ON tb_user(provider, provider_id);

-- Update existing users to have LOCAL provider
UPDATE tb_user SET provider = 'LOCAL' WHERE provider IS NULL;

-- Add comment to explain fields
COMMENT ON COLUMN tb_user.provider IS 'Authentication provider: LOCAL, GOOGLE, GITHUB';
COMMENT ON COLUMN tb_user.provider_id IS 'Unique ID from OAuth2 provider';
COMMENT ON COLUMN tb_user.provider_email IS 'Email from OAuth2 provider';

