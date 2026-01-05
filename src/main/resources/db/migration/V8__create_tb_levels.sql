-- Criar tabela de níveis/levels
CREATE TABLE tb_levels
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    order_level      INTEGER      NOT NULL UNIQUE,
    name             VARCHAR(255) NOT NULL,
    title            VARCHAR(255) NOT NULL,
    description      VARCHAR(1000) NOT NULL,
    xp_required      INTEGER      NOT NULL,
    icon_url         VARCHAR(500),
    difficulty_level VARCHAR(20)  NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP
);

-- Criar índice para melhor performance nas consultas por ordem
CREATE INDEX idx_levels_order ON tb_levels(order_level);

-- Criar índice para consultas por dificuldade
CREATE INDEX idx_levels_difficulty ON tb_levels(difficulty_level);

-- Inserir níveis iniciais de exemplo
INSERT INTO tb_levels (order_level, name, title, description, xp_required, difficulty_level)
VALUES
    (1, 'Iniciante', 'Aprendiz do Código', 'Você está começando sua jornada no mundo da programação. Todo grande desenvolvedor começou aqui!', 0, 'EASY'),
    (2, 'Explorador', 'Explorador de Sintaxe', 'Você está descobrindo as estruturas básicas da programação. Continue explorando!', 100, 'EASY'),
    (3, 'Praticante', 'Praticante Dedicado', 'Sua dedicação está começando a dar frutos. As práticas regulares fazem a diferença!', 300, 'EASY'),
    (4, 'Conhecedor', 'Conhecedor das Estruturas', 'Você domina as estruturas fundamentais. É hora de aprofundar seus conhecimentos!', 600, 'MEDIUM'),
    (5, 'Habilidoso', 'Programador Habilidoso', 'Suas habilidades estão se consolidando. Você resolve problemas com confiança!', 1000, 'MEDIUM'),
    (6, 'Experiente', 'Desenvolvedor Experiente', 'Sua experiência é notável. Você enfrenta desafios complexos com maestria!', 1500, 'MEDIUM'),
    (7, 'Especialista', 'Especialista em Código', 'Você é reconhecido por sua expertise. Problemas difíceis são sua especialidade!', 2200, 'HARD'),
    (8, 'Mestre', 'Mestre do Desenvolvimento', 'Poucos chegam até aqui. Você domina múltiplos paradigmas e tecnologias!', 3000, 'HARD'),
    (9, 'Lendário', 'Lenda Viva', 'Seu nome é lendário na comunidade. Você inspira outros desenvolvedores!', 4000, 'EXPERT'),
    (10, 'Transcendente', 'Arquiteto Supremo', 'Você transcendeu os limites. Sua maestria é incomparável no reino do código!', 5500, 'EXPERT');

