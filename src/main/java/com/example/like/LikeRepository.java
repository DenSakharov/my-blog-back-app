package com.example.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikeRepository {

    private final JdbcTemplate jdbcTemplate;

    public LikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long getLikesCount(long postId) {
        Long count = jdbcTemplate.queryForObject("""
                SELECT likes_count
                FROM posts
                WHERE id = ?
                """, Long.class, postId);

        if (count == null) {
            throw new IllegalArgumentException("Post not found: " + postId);
        }

        return count;
    }

    public long addLike(long postId) {
        Long count = jdbcTemplate.queryForObject("""
                UPDATE posts
                SET likes_count = likes_count + 1
                WHERE id = ?
                RETURNING likes_count
                """, Long.class, postId);

        if (count == null) {
            throw new IllegalArgumentException("Post not found: " + postId);
        }

        return count;
    }
}