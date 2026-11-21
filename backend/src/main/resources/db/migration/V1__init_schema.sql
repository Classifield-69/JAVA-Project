-- V1: Начална схема на GameCollection

-- Потребители (собствен акаунт или Google)
CREATE TABLE users (
    id                    BIGSERIAL PRIMARY KEY,
    email                 VARCHAR(254) NOT NULL UNIQUE,
    username              VARCHAR(30)  NOT NULL UNIQUE,
    password_hash         VARCHAR(100),                          -- BCrypt хеш; празно за Google потребители
    auth_provider         VARCHAR(20)  NOT NULL DEFAULT 'LOCAL', -- LOCAL или GOOGLE
    provider_id           VARCHAR(100),                          -- ID от Google
    avatar_url            VARCHAR(500),
    role                  VARCHAR(20)  NOT NULL DEFAULT 'USER',  -- USER или ADMIN
    enabled               BOOLEAN      NOT NULL DEFAULT TRUE,    -- false = блокиран
    failed_login_attempts INT          NOT NULL DEFAULT 0,       -- за заключване след грешни опити
    locked_until          TIMESTAMP,
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_auth_provider CHECK (auth_provider IN ('LOCAL', 'GOOGLE')),
    CONSTRAINT chk_role CHECK (role IN ('USER', 'ADMIN')),
    CONSTRAINT chk_local_password CHECK (auth_provider <> 'LOCAL' OR password_hash IS NOT NULL)
);

-- Справочни таблици
CREATE TABLE genres (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE platforms (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Каталог с игри (добавят се със seed миграции)
CREATE TABLE games (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(150) NOT NULL,
    developer    VARCHAR(100),
    release_date DATE,
    description  TEXT,
    cover_url    VARCHAR(500),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Игра <-> жанр (много към много)
CREATE TABLE game_genres (
    game_id  BIGINT NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    genre_id BIGINT NOT NULL REFERENCES genres(id),
    PRIMARY KEY (game_id, genre_id)
);

-- Игра <-> платформа (много към много): на кои платформи е излязла играта
CREATE TABLE game_platforms (
    game_id     BIGINT NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    platform_id BIGINT NOT NULL REFERENCES platforms(id),
    PRIMARY KEY (game_id, platform_id)
);

-- Колекцията на потребителя: OWNED или WISHLIST
CREATE TABLE user_games (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    game_id     BIGINT      NOT NULL,
    platform_id BIGINT      NOT NULL,
    type        VARCHAR(20) NOT NULL,
    added_at    TIMESTAMP   NOT NULL DEFAULT NOW(),
    -- играта трябва да е излязла на избраната платформа
    CONSTRAINT fk_user_games_game_platform
        FOREIGN KEY (game_id, platform_id)
        REFERENCES game_platforms (game_id, platform_id) ON DELETE CASCADE,
    -- една игра на една платформа само веднъж за потребител
    CONSTRAINT uq_user_game_platform UNIQUE (user_id, game_id, platform_id),
    CONSTRAINT chk_user_games_type CHECK (type IN ('OWNED', 'WISHLIST'))
);

-- Ревюта
CREATE TABLE reviews (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT   NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    game_id    BIGINT   NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    rating     SMALLINT NOT NULL,
    text       VARCHAR(2000),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    -- едно ревю на игра от потребител
    CONSTRAINT uq_review_user_game UNIQUE (user_id, game_id),
    CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 10)
);

-- Индекси за по-бързо търсене
CREATE INDEX idx_games_title ON games (LOWER(title));
CREATE INDEX idx_user_games_user ON user_games (user_id);
CREATE INDEX idx_reviews_game ON reviews (game_id);