package com.example.dao;

import com.example.exception.CommentNotFoundException;
import com.example.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

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

    public Optional<Comment> findById(long postId, long commentId) {
        String sql = """
                SELECT id, text, post_id
                FROM comments
                WHERE id = ? AND post_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                this::mapRow,
                commentId,
                postId
        ).stream().findFirst();
    }

    public Comment create(long postId, String text) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                    INSERT INTO comments (text, post_id)
                    VALUES (?, ?)
                    """,
                    new String[]{"id"}
            );

            statement.setString(1, text);
            statement.setLong(2, postId);

            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();

        if (key == null) {
            throw new IllegalStateException("Comment id was not generated");
        }

        return findById(postId, key.longValue())
                .orElseThrow(() ->
                        new IllegalStateException("Created comment was not found"));
    }

    public Comment update(long postId, long commentId, String text) {
        int updatedRows = jdbcTemplate.update(
                """
                UPDATE comments
                SET text = ?
                WHERE id = ? AND post_id = ?
                """,
                text,
                commentId,
                postId
        );

        if (updatedRows == 0) {
            throw new IllegalArgumentException(
                    "Comment not found: id=" + commentId + ", postId=" + postId
            );
        }

        return findById(postId, commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    public void delete(long postId, long commentId) {
        int deletedRows = jdbcTemplate.update(
                """
                DELETE FROM comments
                WHERE id = ? AND post_id = ?
                """,
                commentId,
                postId
        );

        if (deletedRows == 0) {
            throw new CommentNotFoundException(commentId);
        }
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