package com.example.dao;

import com.example.config.TestDatabaseConfig;
import com.example.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(
        scripts = "/schema.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
@SpringJUnitConfig(TestDatabaseConfig.class)
class CommentDaoIntegrationTest extends AbstractDaoIntegrationTest {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private long postId;

    @BeforeEach
    void setUpPost() {
        jdbcTemplate.update(
                """
                INSERT INTO posts (title, text)
                VALUES (?, ?)
                """,
                "Test post",
                "Test text"
        );

        postId = jdbcTemplate.queryForObject(
                "SELECT MAX(id) FROM posts",
                Long.class
        );
    }

    @Test
    void create_shouldInsertComment() {
        Comment created = commentDao.create(postId, "First comment");

        assertNotNull(created);
        assertTrue(created.id() > 0);
        assertEquals("First comment", created.text());
        assertEquals(postId, created.postId());
    }

    @Test
    void findById_shouldReturnComment() {
        Comment created = commentDao.create(postId, "Test comment");

        Comment found = commentDao.findById(postId, created.id());

        assertEquals(created.id(), found.id());
        assertEquals("Test comment", found.text());
        assertEquals(postId, found.postId());
    }

    @Test
    void findByPostId_shouldReturnCommentsInIdOrder() {
        commentDao.create(postId, "First comment");
        commentDao.create(postId, "Second comment");

        List<Comment> comments = commentDao.findByPostId(postId);

        assertEquals(2, comments.size());
        assertEquals("First comment", comments.get(0).text());
        assertEquals("Second comment", comments.get(1).text());
    }

    @Test
    void update_shouldChangeCommentText() {
        Comment created = commentDao.create(postId, "Old text");

        Comment updated = commentDao.update(
                postId,
                created.id(),
                "New text"
        );

        assertEquals(created.id(), updated.id());
        assertEquals("New text", updated.text());
        assertEquals(postId, updated.postId());
    }

    @Test
    void delete_shouldRemoveComment() {
        Comment created = commentDao.create(postId, "Comment to delete");

        commentDao.delete(postId, created.id());

        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM comments
                WHERE id = ?
                """,
                Integer.class,
                created.id()
        );

        assertEquals(0, count);
    }

    @Test
    void findById_shouldNotReturnCommentOfAnotherPost() {
        Comment created = commentDao.create(postId, "Private comment");

        jdbcTemplate.update(
                """
                INSERT INTO posts (title, text)
                VALUES (?, ?)
                """,
                "Another post",
                "Another text"
        );

        long anotherPostId = jdbcTemplate.queryForObject(
                "SELECT MAX(id) FROM posts",
                Long.class
        );

        assertThrows(
                Exception.class,
                () -> commentDao.findById(anotherPostId, created.id())
        );
    }
}