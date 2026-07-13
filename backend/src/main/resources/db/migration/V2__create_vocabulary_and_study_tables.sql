ALTER TABLE users
    ADD COLUMN email VARCHAR(120) NULL AFTER username,
    ADD COLUMN avatar_text VARCHAR(8) NOT NULL DEFAULT '词' AFTER nickname,
    MODIFY COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    MODIFY COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

ALTER TABLE users
    ADD CONSTRAINT uk_users_email UNIQUE (email);

CREATE TABLE user_settings (
    user_id BIGINT NOT NULL,
    daily_new_words INT NOT NULL DEFAULT 20,
    daily_review_limit INT NOT NULL DEFAULT 80,
    preferred_level VARCHAR(10) NOT NULL DEFAULT 'BOTH',
    reminder_time TIME NOT NULL DEFAULT '20:30:00',
    sound_enabled BIT(1) NOT NULL DEFAULT b'1',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (user_id),
    CONSTRAINT fk_user_settings_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE words (
    id BIGINT NOT NULL AUTO_INCREMENT,
    word VARCHAR(100) NOT NULL,
    phonetic VARCHAR(160) NOT NULL,
    pronunciation_url VARCHAR(500) NULL,
    brief_definition VARCHAR(500) NOT NULL,
    usage_note TEXT NOT NULL,
    memory_tip TEXT NOT NULL,
    source VARCHAR(500) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_words_word (word),
    KEY idx_words_brief_definition (brief_definition(100))
);

CREATE TABLE word_levels (
    word_id BIGINT NOT NULL,
    level VARCHAR(10) NOT NULL,
    PRIMARY KEY (word_id, level),
    KEY idx_word_levels_level (level),
    CONSTRAINT fk_word_levels_word FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE CASCADE
);

CREATE TABLE word_senses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    word_id BIGINT NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    part_of_speech VARCHAR(30) NOT NULL,
    definition_cn VARCHAR(1000) NOT NULL,
    definition_en VARCHAR(1000) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_word_senses_word (word_id, sort_order),
    CONSTRAINT fk_word_senses_word FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE CASCADE
);

CREATE TABLE word_examples (
    id BIGINT NOT NULL AUTO_INCREMENT,
    word_id BIGINT NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    sentence VARCHAR(1500) NOT NULL,
    translation VARCHAR(1500) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_word_examples_word (word_id, sort_order),
    CONSTRAINT fk_word_examples_word FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE CASCADE
);

CREATE TABLE word_collocations (
    word_id BIGINT NOT NULL,
    sort_order INT NOT NULL,
    collocation VARCHAR(300) NOT NULL,
    PRIMARY KEY (word_id, sort_order),
    CONSTRAINT fk_word_collocations_word FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE CASCADE
);

CREATE TABLE user_word_progress (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    word_id BIGINT NOT NULL,
    mastery VARCHAR(20) NOT NULL DEFAULT 'NEW',
    interval_days INT NOT NULL DEFAULT 0,
    ease_factor DECIMAL(5,2) NOT NULL DEFAULT 2.50,
    next_review_at DATETIME(6) NULL,
    exposure_count INT NOT NULL DEFAULT 0,
    correct_count INT NOT NULL DEFAULT 0,
    lapse_count INT NOT NULL DEFAULT 0,
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_progress_user_word (user_id, word_id),
    KEY idx_progress_due (user_id, next_review_at),
    KEY idx_progress_mastery (user_id, mastery),
    CONSTRAINT fk_progress_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_progress_word FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE CASCADE
);

CREATE TABLE study_sessions (
    id VARCHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    mode VARCHAR(10) NOT NULL,
    total INT NOT NULL,
    completed INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    completed_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    KEY idx_study_sessions_user (user_id, mode, status, created_at),
    CONSTRAINT fk_study_sessions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE study_session_words (
    session_id VARCHAR(36) NOT NULL,
    word_id BIGINT NOT NULL,
    position INT NOT NULL,
    PRIMARY KEY (session_id, word_id),
    UNIQUE KEY uk_session_position (session_id, position),
    CONSTRAINT fk_session_words_session FOREIGN KEY (session_id) REFERENCES study_sessions (id) ON DELETE CASCADE,
    CONSTRAINT fk_session_words_word FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE CASCADE
);

CREATE TABLE review_answers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    session_id VARCHAR(36) NOT NULL,
    word_id BIGINT NOT NULL,
    rating VARCHAR(10) NOT NULL,
    response_time_ms INT NOT NULL,
    interval_days INT NOT NULL,
    mastery VARCHAR(20) NOT NULL,
    next_review_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_answer_user_session_word (user_id, session_id, word_id),
    KEY idx_answers_user_created (user_id, created_at),
    CONSTRAINT fk_answers_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_session FOREIGN KEY (session_id) REFERENCES study_sessions (id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_word FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE CASCADE
);

CREATE TABLE refresh_tokens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token_hash CHAR(64) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    revoked_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_tokens_hash (token_hash),
    KEY idx_refresh_tokens_user (user_id, expires_at),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
