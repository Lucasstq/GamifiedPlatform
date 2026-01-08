-- Índices otimizados para consultas de ranking global
-- Índice composto para ordenar por level e XP (usado no ranking geral)
CREATE INDEX idx_player_character_ranking ON tb_player_character(level DESC, xp DESC);

-- Índice para buscar personagem por user_id (já existe UNIQUE, mas explícito para queries)
-- O índice único já existe, não precisamos criar outro

-- Índice para consultas de XP (usado em análises e rankings parciais)
CREATE INDEX idx_player_character_xp ON tb_player_character(xp DESC);

-- Comentário explicativo
COMMENT ON INDEX idx_player_character_ranking IS 'Índice composto para otimizar consultas de ranking global ordenadas por level e XP';

