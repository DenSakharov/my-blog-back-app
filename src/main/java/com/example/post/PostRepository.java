package com.example.post;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public List<PostResponse> findPosts(
            String search,
            int pageNumber,
            int pageSize
    ) {
        int offset = (pageNumber - 1) * pageSize;
        String searchPattern = "%" + search + "%";

        String sql = """
                SELECT id, title, text
                FROM posts
                WHERE title ILIKE ?
                   OR text ILIKE ?
                ORDER BY id DESC
                LIMIT ? OFFSET ?
                """;

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) -> {
                    long postId = resultSet.getLong("id");

                    return new PostResponse(
                            postId,
                            resultSet.getString("title"),
                            resultSet.getString("text"),
                            findTags(postId),
                            0,
                            0
                    );
                },
                searchPattern,
                searchPattern,
                pageSize,
                offset
        );
    }

    public int countPosts(String search) {
        String searchPattern = "%" + search + "%";

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
    public PostResponse update(long id, PostUpdateRequest request) {
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

        for (String tag : request.tags()) {
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

    public PostResponse findById(long id) {
        return jdbcTemplate.queryForObject(
                """
                SELECT id, title, text
                FROM posts
                WHERE id = ?
                """,
                (rs, rowNum) -> new PostResponse(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("text"),
                        findTags(id),
                        0,
                        0
                ),
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
}