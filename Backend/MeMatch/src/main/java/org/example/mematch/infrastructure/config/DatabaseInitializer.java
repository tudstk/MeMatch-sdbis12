package org.example.mematch.infrastructure.config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initializeDatabase() {
        createUsersTable();
        createMemesTable();
        createCommentsTable();
        createLikesTable();
        createMatchesTable();
        System.out.println("✅ Database tables ensured (via DatabaseInitializer)");
    }

    private void createUsersTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(50) NOT NULL UNIQUE,
                email VARCHAR(255) NOT NULL UNIQUE,
                password_hash VARCHAR(255) NOT NULL,
                description TEXT,
                image_url TEXT
            );
        """);
    }

    private void createMemesTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS memes (
                id SERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL,
                image_url TEXT NOT NULL,
                caption TEXT,
                CONSTRAINT fk_meme_user FOREIGN KEY (user_id)
                    REFERENCES users(id)
                    ON DELETE CASCADE
            );
        """);
    }

    private void createCommentsTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS comments (
                id SERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL,
                meme_id BIGINT NOT NULL,
                content TEXT NOT NULL,
                CONSTRAINT fk_comment_user FOREIGN KEY (user_id)
                    REFERENCES users(id)
                    ON DELETE CASCADE,
                CONSTRAINT fk_comment_meme FOREIGN KEY (meme_id)
                    REFERENCES memes(id)
                    ON DELETE CASCADE
            );
        """);
    }

    private void createLikesTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS likes (
                id SERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL,
                meme_id BIGINT NOT NULL,
                CONSTRAINT fk_like_user FOREIGN KEY (user_id)
                    REFERENCES users(id)
                    ON DELETE CASCADE,
                CONSTRAINT fk_like_meme FOREIGN KEY (meme_id)
                    REFERENCES memes(id)
                    ON DELETE CASCADE,
                CONSTRAINT unique_user_meme_like UNIQUE (user_id, meme_id)
            );
        """);
    }

    private void createMatchesTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS matches (
                id SERIAL PRIMARY KEY,
                user1_id BIGINT NOT NULL,
                user2_id BIGINT NOT NULL,
                matched BOOLEAN NOT NULL DEFAULT TRUE,
                CONSTRAINT fk_match_user1 FOREIGN KEY (user1_id)
                    REFERENCES users(id)
                    ON DELETE CASCADE,
                CONSTRAINT fk_match_user2 FOREIGN KEY (user2_id)
                    REFERENCES users(id)
                    ON DELETE CASCADE,
                CONSTRAINT unique_match_pair UNIQUE (user1_id, user2_id)
            );
        """);
    }
}
