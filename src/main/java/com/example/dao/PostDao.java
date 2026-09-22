package com.example.dao;

import com.example.dto.post.PostImage;
import com.example.exception.PostNotFoundException;
import com.example.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class PostDao {

    private final JdbcTemplate jdbcTemplate;

    public PostDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Post save(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                    INSERT INTO posts (title, text)
                    VALUES (?, ?)
                    """,
                    new String[]{"id"}
            );

            statement.setString(1, post.title());
            statement.setString(2, post.text());

            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();

        if (key == null) {
            throw new IllegalStateException("Post id was not generated");
        }

        long postId = key.longValue();

        if (post.tags() != null) {
            for (String tag : post.tags()) {
                jdbcTemplate.update(
                        """
                        INSERT INTO post_tags (post_id, tag)
                        VALUES (?, ?)
                        """,
                        postId,
                        tag
                );
            }
        }

        return findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    public List<Post> findPosts(
            String search,
            int pageNumber,
            int pageSize
    ) {
        int offset = (pageNumber - 1) * pageSize;
        String searchPattern = "%" + (search == null ? "" : search) + "%";

        String sql = """
            SELECT id, title, text,
                   likes_count, comments_count,
                   image_content_type
            FROM posts
            WHERE title ILIKE ?
               OR text ILIKE ?
            ORDER BY id DESC
            LIMIT ? OFFSET ?
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapPost(rs),
                searchPattern,
                searchPattern,
                pageSize,
                offset
        );
    }

    public int countPosts(String search) {
        String searchPattern = "%" + (search == null ? "" : search) + "%";

        String sql = """
                SELECT COUNT(*)
                FROM posts
                WHERE title ILIKE ?
                   OR text ILIKE ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                searchPattern,
                searchPattern
        );

        return count == null ? 0 : count;
    }

    private List<String> findTags(long postId) {
        String sql = """
                SELECT tag
                FROM post_tags
                WHERE post_id = ?
                ORDER BY tag
                """;

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) -> resultSet.getString("tag"),
                postId
        );
    }

    @Transactional
    public Post update(long id, Post post) {
        int updatedRows = jdbcTemplate.update(
                """
                UPDATE posts
                SET title = ?, text = ?
                WHERE id = ?
                """,
                post.title(),
                post.text(),
                id
        );

        if (updatedRows == 0) {
            throw new IllegalArgumentException("Post not found: " + id);
        }

        jdbcTemplate.update(
                "DELETE FROM post_tags WHERE post_id = ?",
                id
        );

        List<String> tags = post.tags() == null
                ? List.of()
                : post.tags();

        for (String tag : tags) {
            jdbcTemplate.update(
                    """
                    INSERT INTO post_tags (post_id, tag)
                    VALUES (?, ?)
                    """,
                    id,
                    tag
            );
        }

        return findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    public Optional<Post> findById(long id) {
        return jdbcTemplate.query(
                """
                SELECT id, title, text,
                       likes_count, comments_count,
                       image_content_type
                FROM posts
                WHERE id = ?
                """,
                (rs, rowNum) -> mapPost(rs),
                id
        ).stream().findFirst();
    }

    @Transactional
    public int updateImage(
            long postId,
            byte[] imageData,
            String contentType
    ) {
        return jdbcTemplate.update(
                """
                UPDATE posts
                SET image_data = ?,
                    image_content_type = ?
                WHERE id = ?
                """,
                imageData,
                contentType,
                postId
        );
    }

    public Optional<PostImage> findImageByPostId(long postId) {
        List<PostImage> images = jdbcTemplate.query(
                """
                SELECT image_data, image_content_type
                FROM posts
                WHERE id = ?
                """,
                (rs, rowNum) -> new PostImage(
                        rs.getBytes("image_data"),
                        rs.getString("image_content_type")
                ),
                postId
        );

        return images.stream().findFirst();
    }

    @Transactional
    public void delete(long id) {
        int deletedRows = jdbcTemplate.update(
                "DELETE FROM posts WHERE id = ?",
                id
        );

        if (deletedRows == 0) {
            throw new IllegalArgumentException("Post not found: " + id);
        }
    }

    private Post mapPost(
            java.sql.ResultSet rs
    ) throws java.sql.SQLException {
        long postId = rs.getLong("id");

        return new Post(
                postId,
                rs.getString("title"),
                rs.getString("text"),
                findTags(postId),
                rs.getLong("likes_count"),
                rs.getLong("comments_count"),
                rs.getString("image_content_type")
        );
    }
}