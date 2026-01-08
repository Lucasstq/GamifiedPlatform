-- Criação da tabela de notificações
CREATE TABLE tb_notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT false,
    reference_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE
);

-- Índices para performance
CREATE INDEX idx_notification_user_read ON tb_notifications(user_id, is_read);
CREATE INDEX idx_notification_created_at ON tb_notifications(created_at);

-- Comentários
COMMENT ON TABLE tb_notifications IS 'Tabela de notificações do sistema. Preparação para WebSocket futuro.';
COMMENT ON COLUMN tb_notifications.type IS 'Tipo de notificação: MISSION_EVALUATED, LEVEL_UP, BADGE_UNLOCKED, BOSS_EVALUATED, BOSS_UNLOCKED, GRIMOIRE_UNLOCKED';
COMMENT ON COLUMN tb_notifications.reference_id IS 'ID de referência (missão, badge, level, boss, etc)';

