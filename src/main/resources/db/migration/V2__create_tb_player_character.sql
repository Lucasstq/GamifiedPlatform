CREATE TABLE tb_player_character
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name       VARCHAR(100) NOT NULL UNIQUE,
    level      INTEGER      NOT NULL DEFAULT 1,
    xp         INTEGER       NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    user_id    BIGINT       NOT NULL,
        CONSTRAINT fk_character_user FOREIGN KEY (user_id)
        REFERENCES tb_user (id) ON DELETE CASCADE
);