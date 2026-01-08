-- Migration V18: Create user_badges table

CREATE TABLE tb_user_badges
(
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT    NOT NULL,
    badge_id            BIGINT    NOT NULL,
    unlocked_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    unlocked_by_boss_id BIGINT,

    CONSTRAINT fk_user_badge_user FOREIGN KEY (user_id)
        REFERENCES tb_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_badge_badge FOREIGN KEY (badge_id)
        REFERENCES tb_badges (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_badge_boss FOREIGN KEY (unlocked_by_boss_id)
        REFERENCES tb_bosses (id) ON DELETE SET NULL,
    CONSTRAINT uk_user_badge UNIQUE (user_id, badge_id)
);

CREATE INDEX idx_user_badges_user_id ON tb_user_badges (user_id);
CREATE INDEX idx_user_badges_badge_id ON tb_user_badges (badge_id);
CREATE INDEX idx_user_badges_unlocked_at ON tb_user_badges (unlocked_at);

