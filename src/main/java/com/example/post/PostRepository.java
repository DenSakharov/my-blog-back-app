package com.example.post;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public PostResponse save(PostCreateRequest request) {
        Long postId = null;
        try {
         postId = jdbcTemplate.queryForObject(
                """
                INSERT INTO posts (title, text)
                VALUES (?, ?)
                RETURNING id
                """,
                Long.class,
                request.title(),
                request.text()
        );
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        String insertTagSql = """
                INSERT INTO post_tags (post_id, tag)
                VALUES (?, ?)
                """;

        if (request.tags() != null) {
            for (String tag : request.tags()) {
                jdbcTemplate.update(insertTagSql, postId, tag);
            }
        }

        return new PostResponse(
                postId,
                request.title(),
                request.text(),
                request.tags(),
                0,
                0
        );
    }
}