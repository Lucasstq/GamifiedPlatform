-- Criar tabela de scopes (permissões)
CREATE TABLE tb_scopes
(
    id   BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Criar tabela de relacionamento Many-to-Many entre User e Scopes
CREATE TABLE tb_user_scopes
(
    user_id   BIGINT NOT NULL,
    scopes_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, scopes_id),
    CONSTRAINT fk_user_scopes_user FOREIGN KEY (user_id)
        REFERENCES tb_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_scopes_scope FOREIGN KEY (scopes_id)
        REFERENCES tb_scopes (id) ON DELETE CASCADE
);

-- Criar índices para melhor performance
CREATE INDEX idx_user_scopes_user ON tb_user_scopes(user_id);
CREATE INDEX idx_user_scopes_scope ON tb_user_scopes(scopes_id);


