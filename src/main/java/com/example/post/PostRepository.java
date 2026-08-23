package com.example.post;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}