package com.example.comment;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CommentResponse> findByPostId(long postId) {
        String sql = """
                SELECT id, text, post_id
                FROM comments
                WHERE post_id = ?
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, this::mapRow, postId);
    }

    public CommentResponse findById(long postId, long commentId) {
        String sql = """
                SELECT id, text, post_id
                FROM comments
                WHERE id = ? AND post_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                commentId,
                postId
        );
    }

    public CommentResponse create(long postId, String text) {
        String sql = """
                INSERT INTO comments (text, post_id)
                VALUES (?, ?)
                RETURNING id, text, post_id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                text,
                postId
        );
    }

    public CommentResponse update(
            long commentId,
            long postId,
            String text
    ) {
        String sql = """
        UPDATE comments
        SET text = ?
        WHERE id = ? AND post_id = ?
        RETURNING id, text, post_id
        """;

        return jdbcTemplate.queryForObject(
                sql,
                commentRowMapper,
                text,
                commentId,
                postId
        );
    }

    public void delete(long postId, long commentId) {
        String sql = """
                DELETE FROM comments
                WHERE id = ? AND post_id = ?
                """;

        jdbcTemplate.update(sql, commentId, postId);
    }

    private CommentResponse mapRow(
            java.sql.ResultSet rs,
            int rowNum
    ) throws java.sql.SQLException {
        return new CommentResponse(
                rs.getLong("id"),
                rs.getString("text"),
                rs.getLong("post_id")
        );
    }

    private final RowMapper<CommentResponse> commentRowMapper =
            (rs, rowNum) -> new CommentResponse(
                    rs.getLong("id"),
                    rs.getString("text"),
                    rs.getLong("post_id")
            );
}