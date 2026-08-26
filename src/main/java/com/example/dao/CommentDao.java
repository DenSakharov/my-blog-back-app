package com.example.dao;

import com.example.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentDao {

    private final JdbcTemplate jdbcTemplate;

    public CommentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Comment> findByPostId(long postId) {
        String sql = """
                SELECT id, text, post_id
                FROM comments
                WHERE post_id = ?
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, this::mapRow, postId);
    }

    public Comment findById(long postId, long commentId) {
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

    public Comment create(long postId, String text) {
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

    public Comment update(long postId, long commentId, String text) {
        String sql = """
                UPDATE comments
                SET text = ?
                WHERE id = ? AND post_id = ?
                RETURNING id, text, post_id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
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

    private Comment mapRow(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        return new Comment(
                rs.getLong("id"),
                rs.getString("text"),
                rs.getLong("post_id")
        );
    }
}