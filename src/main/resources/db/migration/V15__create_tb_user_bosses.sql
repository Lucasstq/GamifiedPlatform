-- Migration V15: Create user_bosses table

CREATE TABLE tb_user_bosses
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    boss_id          BIGINT      NOT NULL,
    status           VARCHAR(50) NOT NULL DEFAULT 'LOCKED',
    submission_url   VARCHAR(500),
    submission_notes TEXT,
    feedback         TEXT,
    evaluated_by     BIGINT,
    started_at       TIMESTAMP,
    submitted_at     TIMESTAMP,
    evaluated_at     TIMESTAMP,
    completed_at     TIMESTAMP,
    unlocked_at      TIMESTAMP,

    CONSTRAINT fk_user_boss_user FOREIGN KEY (user_id)
        REFERENCES tb_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_boss_boss FOREIGN KEY (boss_id)
        REFERENCES tb_bosses (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_boss_evaluator FOREIGN KEY (evaluated_by)
        REFERENCES tb_user (id) ON DELETE SET NULL,
    CONSTRAINT uk_user_boss UNIQUE (user_id, boss_id)
);

CREATE INDEX idx_user_bosses_user_id ON tb_user_bosses (user_id);
CREATE INDEX idx_user_bosses_status ON tb_user_bosses (status);
CREATE INDEX idx_user_bosses_boss_id ON tb_user_bosses (boss_id);

