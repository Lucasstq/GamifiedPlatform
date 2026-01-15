-- Migration para adicionar índices compostos otimizados
-- Melhora performance de queries comuns em relacionamentos e buscas

-- Índice para busca de missões por usuário e status
-- Otimiza queries como: SELECT * FROM tb_user_missions WHERE user_id = ? AND status = ?
CREATE INDEX IF NOT EXISTS idx_user_mission_user_status
ON tb_user_missions(user_id, status);

-- Índice para busca de boss fights por usuário e status
-- Otimiza queries como: SELECT * FROM tb_user_bosses WHERE user_id = ? AND status = ?
CREATE INDEX IF NOT EXISTS idx_user_boss_user_status
ON tb_user_bosses(user_id, status);

-- Índice para busca de refresh tokens ativos por usuário
-- Otimiza queries como: SELECT * FROM tb_refresh_tokens WHERE user_id = ? AND revoked = false
CREATE INDEX IF NOT EXISTS idx_refresh_token_user_active
ON tb_refresh_tokens(user_id, revoked)
WHERE revoked = false;

-- Índice para busca de badges por usuário
-- Otimiza queries como: SELECT * FROM tb_user_badges WHERE user_id = ?
CREATE INDEX IF NOT EXISTS idx_user_badge_user_unlocked
ON tb_user_badges(user_id, unlocked_at);

-- Índice para busca eficiente de usuários por email e deleted
-- Otimiza login e verificações de email
CREATE INDEX IF NOT EXISTS idx_user_email_deleted
ON tb_user(email, deleted)
WHERE deleted = false;

-- Índice para busca eficiente de usuários por username e active
-- Otimiza autenticação
CREATE INDEX IF NOT EXISTS idx_user_username_active
ON tb_user(username, active)
WHERE active = true AND deleted = false;

-- Índice para busca de logs de auditoria por usuário e data
-- Otimiza queries de auditoria e investigação
CREATE INDEX IF NOT EXISTS idx_security_audit_user_timestamp
ON tb_security_audit_log(user_id, timestamp DESC);

-- Índice para busca de logs de auditoria por tipo de evento
-- Otimiza análises de segurança
CREATE INDEX IF NOT EXISTS idx_security_audit_event_timestamp
ON tb_security_audit_log(event_type, timestamp DESC);

