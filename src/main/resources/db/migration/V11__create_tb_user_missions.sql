-- Migration V11: Create user_missions table
CREATE TABLE tb_user_missions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    mission_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DISPONIVEL',
    submission_url VARCHAR(500),
    submission_notes TEXT,
    feedback TEXT,
    evaluated_by BIGINT,
    started_at TIMESTAMP,
    submitted_at TIMESTAMP,
    evaluated_at TIMESTAMP,
    completed_at TIMESTAMP,

    CONSTRAINT fk_user_mission_user FOREIGN KEY (user_id)
        REFERENCES tb_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_mission_mission FOREIGN KEY (mission_id)
        REFERENCES tb_missions (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_mission_evaluator FOREIGN KEY (evaluated_by)
        REFERENCES tb_user (id) ON DELETE SET NULL,
    CONSTRAINT uk_user_mission UNIQUE (user_id, mission_id)
);

CREATE INDEX idx_user_missions_user_id ON tb_user_missions (user_id);
CREATE INDEX idx_user_missions_status ON tb_user_missions (status);
CREATE INDEX idx_user_missions_mission_id ON tb_user_missions (mission_id);

