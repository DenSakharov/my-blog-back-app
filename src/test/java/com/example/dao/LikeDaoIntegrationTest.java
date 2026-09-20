package com.example.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

class LikeDaoIntegrationTest extends AbstractDaoIntegrationTest {

    @Autowired
    private LikeDao likeDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private long postId;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM comments");
        jdbcTemplate.update("DELETE FROM posts");

        jdbcTemplate.update(
                """
                INSERT INTO posts (title, text, likes_count)
                VALUES (?, ?, ?)
                """,
                "Test post",
                "Test text",
                0
        );

        postId = jdbcTemplate.queryForObject(
                "SELECT MAX(id) FROM posts",
                Long.class
        );
    }

    @Test
    void getLikesCount_shouldReturnZeroForNewPost() {
        long likesCount = likeDao.getLikesCount(postId);

        assertEquals(0L, likesCount);
    }

    @Test
    void addLike_shouldIncrementLikesCount() {
        long likesCount = likeDao.addLike(postId);

        assertEquals(1L, likesCount);
        assertEquals(1L, likeDao.getLikesCount(postId));
    }

    @Test
    void addLike_shouldIncrementLikesCountSeveralTimes() {
        likeDao.addLike(postId);
        likeDao.addLike(postId);
        likeDao.addLike(postId);

        assertEquals(3L, likeDao.getLikesCount(postId));
    }

    @Test
    void getLikesCount_shouldReturnExistingLikesCount() {
        jdbcTemplate.update(
                """
                UPDATE posts
                SET likes_count = ?
                WHERE id = ?
                """,
                5,
                postId
        );

        assertEquals(5L, likeDao.getLikesCount(postId));
    }

    @Test
    void addLike_shouldThrowExceptionForMissingPost() {
        long missingPostId = 999999L;

        assertThrows(
                IllegalArgumentException.class,
                () -> likeDao.addLike(missingPostId)
        );
    }
}