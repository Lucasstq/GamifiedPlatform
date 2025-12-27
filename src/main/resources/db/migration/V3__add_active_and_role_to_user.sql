ALTER TABLE tb_user
    ADD COLUMN role VARCHAR(50),
ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE tb_user
SET role = 'ROLE_USER'
WHERE role IS NULL;

CREATE INDEX idx_user_role ON tb_user (role);

