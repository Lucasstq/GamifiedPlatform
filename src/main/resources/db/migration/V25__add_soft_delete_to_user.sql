-- Adicionar campos de soft delete para usuários
ALTER TABLE tb_user ADD COLUMN deleted BOOLEAN DEFAULT false;
ALTER TABLE tb_user ADD COLUMN deleted_at TIMESTAMP;

-- Criar índice para consultas de usuários não deletados
CREATE INDEX idx_user_deleted ON tb_user(deleted) WHERE deleted = false;

-- Comentários
COMMENT ON COLUMN tb_user.deleted IS 'Indica se o usuário foi deletado (soft delete)';
COMMENT ON COLUMN tb_user.deleted_at IS 'Data e hora em que o usuário foi deletado';

