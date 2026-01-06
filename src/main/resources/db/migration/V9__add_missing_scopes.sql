-- Inserir novos scopes no banco de dados (se ainda não existirem)
-- Esta migration adiciona os scopes que estavam faltando na V4/V5
INSERT INTO tb_scopes (name)
VALUES ('profile:read'),
       ('profile:write'),
       ('profile:delete'),
       ('character:read'),
       ('character:write'),
       ('levels:read'),
       ('quests:read'),
       ('quests:write'),
       ('quests:complete'),
       ('quests:initiate'),
       ('achievements:read'),
       ('users:read'),
       ('admin:all') ON CONFLICT (name) DO NOTHING;

-- Atribuir o scope 'levels:read' aos usuários que não o possuem
-- Para usuários com ROLE_USER
INSERT INTO tb_user_scopes (user_id, scopes_id)
SELECT u.id, s.id
FROM tb_user u
         CROSS JOIN tb_scopes s
WHERE u.role = 'ROLE_USER'
  AND s.name IN ('levels:read', 'quests:initiate')
  AND NOT EXISTS (
    SELECT 1
    FROM tb_user_scopes us
    WHERE us.user_id = u.id
      AND us.scopes_id = s.id
)
ON CONFLICT (user_id, scopes_id) DO NOTHING;

-- Para usuários com ROLE_MENTOR
INSERT INTO tb_user_scopes (user_id, scopes_id)
SELECT u.id, s.id
FROM tb_user u
         CROSS JOIN tb_scopes s
WHERE u.role = 'ROLE_MENTOR'
  AND s.name IN ('levels:read', 'quests:initiate', 'quests:evaluate', 'pending-quests:read', 'my-evaluations:read')
  AND NOT EXISTS (
    SELECT 1
    FROM tb_user_scopes us
    WHERE us.user_id = u.id
      AND us.scopes_id = s.id
)
ON CONFLICT (user_id, scopes_id) DO NOTHING;

