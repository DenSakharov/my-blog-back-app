package com.example.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikeDao {

    private final JdbcTemplate jdbcTemplate;

    public LikeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long getLikesCount(long postId) {
        Long count = jdbcTemplate.queryForObject(
                """
                SELECT likes_count
                FROM posts
                WHERE id = ?
                """,
                Long.class,
                postId
        );

        if (count == null) {
            throw new IllegalArgumentException("Post not found: " + postId);
        }

        return count;
    }

    public long addLike(long postId) {
        int updatedRows = jdbcTemplate.update(
                """
                UPDATE posts
                SET likes_count = likes_count + 1
                WHERE id = ?
                """,
                postId
        );

        if (updatedRows == 0) {
            throw new IllegalArgumentException("Post not found: " + postId);
        }

        return getLikesCount(postId);
    }
}