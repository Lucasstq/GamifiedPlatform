-- Migration V10: Create missions table

CREATE TABLE tb_missions
(
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    order_number INTEGER      NOT NULL,
    xp_reward    INTEGER      NOT NULL,
    description  TEXT         NOT NULL,
    title        VARCHAR(255) NOT NULL,
    level_id     BIGINT       NOT NULL,
    id           BIGSERIAL PRIMARY KEY,

    CONSTRAINT fk_mission_level FOREIGN KEY (level_id) REFERENCES tb_levels (id) ON DELETE CASCADE
);

CREATE INDEX idx_missions_order ON tb_missions (level_id, order_number);
CREATE INDEX idx_missions_level_id ON tb_missions (level_id);



