-- Índices adicionais para otimização de performance

-- Índices para tb_user_missions
CREATE INDEX IF NOT EXISTS idx_user_mission_status ON tb_user_missions(status);
CREATE INDEX IF NOT EXISTS idx_user_mission_mission_id ON tb_user_missions(mission_id);
CREATE INDEX IF NOT EXISTS idx_user_mission_evaluated_by ON tb_user_missions(evaluated_by);
CREATE INDEX IF NOT EXISTS idx_user_mission_submitted_at ON tb_user_missions(submitted_at) WHERE status = 'AWAITING_EVALUATION';

-- Índices para tb_user_bosses
CREATE INDEX IF NOT EXISTS idx_user_boss_status ON tb_user_bosses(status);
CREATE INDEX IF NOT EXISTS idx_user_boss_boss_id ON tb_user_bosses(boss_id);
CREATE INDEX IF NOT EXISTS idx_user_boss_evaluated_by ON tb_user_bosses(evaluated_by);
CREATE INDEX IF NOT EXISTS idx_user_boss_submitted_at ON tb_user_bosses(submitted_at) WHERE status = 'AWAITING_EVALUATION';

-- Índices para tb_missions
CREATE INDEX IF NOT EXISTS idx_mission_level_order ON tb_missions(level_id, order_number);

-- Índices compostos para tb_player_character (queries de ranking)
CREATE INDEX IF NOT EXISTS idx_character_level_xp ON tb_player_character(level, xp DESC);

-- Índices para tb_user_badges
CREATE INDEX IF NOT EXISTS idx_user_badge_unlocked_at ON tb_user_badges(unlocked_at DESC);

-- Índices para pesquisas de usuário
CREATE INDEX IF NOT EXISTS idx_user_email_active ON tb_user(email) WHERE deleted = false AND active = true;
CREATE INDEX IF NOT EXISTS idx_user_username_active ON tb_user(username) WHERE deleted = false AND active = true;

-- Comentários
COMMENT ON INDEX idx_user_mission_status IS 'Índice para filtrar missões por status';
COMMENT ON INDEX idx_user_mission_submitted_at IS 'Índice para ordenar missões aguardando avaliação';
COMMENT ON INDEX idx_user_boss_status IS 'Índice para filtrar boss fights por status';
COMMENT ON INDEX idx_character_level_xp IS 'Índice composto para queries de ranking';
COMMENT ON INDEX idx_user_email_active IS 'Índice parcial para busca de usuários ativos por email';

