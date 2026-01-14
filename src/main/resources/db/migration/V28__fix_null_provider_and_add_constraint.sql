-- Fix ALL NULL provider values (including deleted users)
UPDATE tb_user
SET provider = 'LOCAL'
WHERE provider IS NULL;

-- Add default value first
ALTER TABLE tb_user
ALTER COLUMN provider SET DEFAULT 'LOCAL';

-- Now add NOT NULL constraint after all NULL values are fixed
ALTER TABLE tb_user
ALTER COLUMN provider SET NOT NULL;

