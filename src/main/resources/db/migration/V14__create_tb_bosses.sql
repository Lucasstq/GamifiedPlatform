-- Migration V14: Create bosses table
CREATE TABLE tb_bosses
(
    id                  BIGSERIAL PRIMARY KEY,
    level_id            BIGINT       NOT NULL UNIQUE,
    name                VARCHAR(255) NOT NULL,
    title               VARCHAR(255) NOT NULL,
    description         TEXT         NOT NULL,
    challenge           TEXT         NOT NULL,
    xp_reward           INTEGER      NOT NULL,
    badge_name          VARCHAR(255) NOT NULL,
    badge_description   VARCHAR(500) NOT NULL,
    image_url           VARCHAR(500),
    badge_icon_url      VARCHAR(500),
    unlocks_next_level  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP,

    CONSTRAINT fk_boss_level FOREIGN KEY (level_id) REFERENCES tb_levels (id) ON DELETE CASCADE
);

CREATE INDEX idx_bosses_level_id ON tb_bosses (level_id);


