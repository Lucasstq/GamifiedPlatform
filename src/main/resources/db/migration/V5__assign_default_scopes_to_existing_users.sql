-- Atribuir scopes padrão para usuários existentes baseado em suas roles

-- Para usuários com ROLE_USER (estudantes)
INSERT INTO tb_user_scopes (user_id, scopes_id)
SELECT u.id, s.id
FROM tb_user u
CROSS JOIN tb_scopes s
WHERE u.role = 'ROLE_USER'
  AND s.name IN (
    'profile:read',
    'profile:write',
    'profile:delete',
    'character:read',
    'character:write',
    'quests:read',
    'quests:complete',
    'achievements:read'
);

-- Para usuários com ROLE_MENTOR (mentores)
INSERT INTO tb_user_scopes (user_id, scopes_id)
SELECT u.id, s.id
FROM tb_user u
CROSS JOIN tb_scopes s
WHERE u.role = 'ROLE_MENTOR'
  AND s.name IN (
    'users:read',
    'profile:read',
    'profile:write',
    'profile:delete',
    'character:read',
    'character:write',
    'quests:read',
    'quests:write',
    'quests:complete',
    'achievements:read'
);

-- Para usuários com ROLE_ADMIN (administradores)
INSERT INTO tb_user_scopes (user_id, scopes_id)
SELECT u.id, s.id
FROM tb_user u
CROSS JOIN tb_scopes s
WHERE u.role = 'ROLE_ADMIN'
  AND s.name = 'admin:all';

-- Comentário
COMMENT ON TABLE tb_user_scopes IS 'Usuários recebem scopes baseados em suas roles por padrão';

