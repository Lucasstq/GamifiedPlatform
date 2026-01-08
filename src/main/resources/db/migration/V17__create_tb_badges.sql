-- Migration V17: Create badges table

CREATE TABLE tb_badges
(
    id          BIGSERIAL PRIMARY KEY,
    level_id    BIGINT       NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL UNIQUE,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    icon_url    VARCHAR(500),
    rarity      VARCHAR(50)  NOT NULL DEFAULT 'EPIC',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP,

    CONSTRAINT fk_badge_level FOREIGN KEY (level_id) REFERENCES tb_levels (id) ON DELETE CASCADE
);

CREATE INDEX idx_badges_level_id ON tb_badges (level_id);
CREATE INDEX idx_badges_rarity ON tb_badges (rarity);

