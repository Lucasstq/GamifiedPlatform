-- Migration V14: Create bosses table
CREATE INDEX idx_bosses_level_id ON tb_bosses (level_id);

);
    CONSTRAINT fk_boss_level FOREIGN KEY (level_id) REFERENCES tb_levels (id) ON DELETE CASCADE

    updated_at          TIMESTAMP,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    unlocks_next_level  BOOLEAN      NOT NULL DEFAULT TRUE,
    badge_icon_url      VARCHAR(500),
    image_url           VARCHAR(500),
    badge_description   VARCHAR(500) NOT NULL,
    badge_name          VARCHAR(255) NOT NULL,
    xp_reward           INTEGER      NOT NULL,
    challenge           TEXT         NOT NULL,
    description         TEXT         NOT NULL,
    title               VARCHAR(255) NOT NULL,
    name                VARCHAR(255) NOT NULL,
    level_id            BIGINT       NOT NULL UNIQUE,
    id                  BIGSERIAL PRIMARY KEY,
(
CREATE TABLE tb_bosses


