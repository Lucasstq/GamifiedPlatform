-- Criar tabela de grimórios (relacionamento com níveis)
CREATE TABLE tb_grimoires
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    level_id        BIGINT       NOT NULL,
    file_name       VARCHAR(255) NOT NULL,
    original_name   VARCHAR(255) NOT NULL,
    file_size       BIGINT       NOT NULL,
    content_type    VARCHAR(100) NOT NULL,
    minio_bucket    VARCHAR(100) NOT NULL,
    minio_object_key VARCHAR(500) NOT NULL,
    description     VARCHAR(1000),
    uploaded_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    uploaded_by     BIGINT,
    CONSTRAINT fk_grimoire_level FOREIGN KEY (level_id) REFERENCES tb_levels (id) ON DELETE CASCADE,
    CONSTRAINT fk_grimoire_uploader FOREIGN KEY (uploaded_by) REFERENCES tb_user (id) ON DELETE SET NULL
);

-- Criar tabela de registro de downloads de grimórios
CREATE TABLE tb_grimoire_downloads
(
    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    grimoire_id    BIGINT NOT NULL,
    user_id        BIGINT NOT NULL,
    downloaded_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_level_at_download INTEGER NOT NULL,
    CONSTRAINT fk_download_grimoire FOREIGN KEY (grimoire_id) REFERENCES tb_grimoires (id) ON DELETE CASCADE,
    CONSTRAINT fk_download_user FOREIGN KEY (user_id) REFERENCES tb_user (id) ON DELETE CASCADE
);

-- Índices para otimizar consultas
CREATE INDEX idx_grimoires_level ON tb_grimoires(level_id);
CREATE INDEX idx_downloads_grimoire ON tb_grimoire_downloads(grimoire_id);
CREATE INDEX idx_downloads_user ON tb_grimoire_downloads(user_id);
CREATE INDEX idx_downloads_date ON tb_grimoire_downloads(downloaded_at DESC);

-- Adicionar scope para acesso a grimórios
-- Nota: Operações admin (upload/delete) usam @IsAdmin, não precisam de scope específico
INSERT INTO tb_scopes (name, description)
VALUES ('grimoire:read', 'Permissão para visualizar e fazer download de grimórios desbloqueados')
ON CONFLICT (name) DO NOTHING;

-- Atribuir scope de grimórios a todos os usuários (ADMIN e USER)
INSERT INTO tb_user_scopes (user_id, scopes_id)
SELECT u.id, s.id
FROM tb_user u, tb_scopes s
WHERE s.name = 'grimoire:read'
  AND NOT EXISTS (
    SELECT 1 FROM tb_user_scopes us
    WHERE us.user_id = u.id AND us.scopes_id = s.id
  );

-- Comentários explicativos
COMMENT ON TABLE tb_grimoires IS 'Armazena metadados dos grimórios (PDFs) associados aos níveis';
COMMENT ON TABLE tb_grimoire_downloads IS 'Registra histórico de downloads de grimórios pelos usuários';
COMMENT ON COLUMN tb_grimoires.minio_object_key IS 'Chave do objeto no MinIO (path completo)';
COMMENT ON COLUMN tb_grimoire_downloads.user_level_at_download IS 'Nível do usuário no momento do download';

