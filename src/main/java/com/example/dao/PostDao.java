package com.example.dao;

import com.example.dto.post.PostCreateRequest;
import com.example.dto.post.PostImage;
import com.example.model.Post;
import com.example.dto.post.PostUpdateRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class PostDao {

    private final JdbcTemplate jdbcTemplate;

    public PostDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Post save(PostCreateRequest request) {
        Long postId = jdbcTemplate.queryForObject(
                """
                INSERT INTO posts (title, text)
                VALUES (?, ?)
                RETURNING id
                """,
                Long.class,
                request.title(),
                request.text()
        );

        if (request.tags() != null) {
            for (String tag : request.tags()) {
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

        return findById(postId);
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
    public Post update(long id, PostUpdateRequest request) {
        int updatedRows = jdbcTemplate.update(
                """
                UPDATE posts
                SET title = ?, text = ?
                WHERE id = ?
                """,
                request.title(),
                request.text(),
                id
        );

        if (updatedRows == 0) {
            throw new IllegalArgumentException("Post not found: " + id);
        }

        jdbcTemplate.update(
                "DELETE FROM post_tags WHERE post_id = ?",
                id
        );

        List<String> tags = request.tags() == null
                ? List.of()
                : request.tags();

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

        return findById(id);
    }

    public Post findById(long id) {
        return jdbcTemplate.queryForObject(
                """
                SELECT id, title, text,
                       likes_count, comments_count,
                       image_content_type
                FROM posts
                WHERE id = ?
                """,
                (rs, rowNum) -> mapPost(rs),
                id
        );
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