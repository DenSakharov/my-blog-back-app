package com.example.dao;

import com.example.fixture.PostTestDataFactory;
import com.example.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PostDaoIntegrationTest extends AbstractDaoIntegrationTest {

    @Autowired
    private PostDao postDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void save_shouldInsertPostWithTags() {
        Post postSaveModel = new Post(
                0L,
                "Java",
                "Spring Framework",
                List.of("java", "spring"),
                0L,
                0L,
                null
        );

        var saved = postDao.save(postSaveModel)
                .orElseThrow();

        assertNotNull(saved);
        assertTrue(saved.id() > 0);
        assertEquals("Java", saved.title());
        assertEquals("Spring Framework", saved.text());
        assertEquals(List.of("java", "spring"), saved.tags());
        assertEquals(0, saved.likesCount());
        assertEquals(0, saved.commentsCount());
    }

    @Test
    void findById_shouldReturnPost() {
        Post postSaveModel = new Post(
                0L,
                "Java",
                "Spring Framework",
                List.of("java", "spring"),
                0L,
                0L,
                null
        );

        var saved = postDao.save(postSaveModel)
                .orElseThrow();

        Optional<Post> result = postDao.findById(saved.id());

        assertTrue(result.isPresent());

        var found = result.get();

        assertEquals(saved.id(), found.id());
        assertEquals("Java", found.title());
        assertEquals("Spring Framework", found.text());
        assertEquals(List.of("java", "spring"), found.tags());
    }

    @Test
    void findPosts_shouldFilterBySearchText() {
        Post javaPost = new Post(
                0L,
                "Java Programming",
                "Spring Framework",
                List.of("java", "spring"),
                0L,
                0L,
                null
        );

        Post cookingPost = new Post(
                0L,
                "Cooking Recipes",
                "How to cook pasta",
                List.of("cooking"),
                0L,
                0L,
                null
        );

        postDao.save(javaPost);
        postDao.save(cookingPost);

        List<Post> result =
                postDao.findPosts("Java", 1, 10);

        assertEquals(1, result.size());
        assertEquals("Java Programming", result.get(0).title());
    }

    @Test
    void findPosts_shouldSupportPagination() {
        postDao.save(PostTestDataFactory.createPost());
        postDao.save(PostTestDataFactory.createPost());
        postDao.save(PostTestDataFactory.createPost());

        List<Post> page = postDao.findPosts(null, 1, 2);

        assertEquals(2, page.size());
    }

    @Test
    void countPosts_shouldReturnMatchingCount() {
        Post post = PostTestDataFactory.createPost();
        postDao.save(post);
        postDao.save(PostTestDataFactory.createPost());

        assertEquals(1, postDao.countPosts(post.title()));
    }

    @Test
    void update_shouldChangePostAndTags() {
        Post saved = postDao.save(PostTestDataFactory.createPost())
                .orElseThrow();

        Post updated = postDao.update(
                saved.id(),
                new Post(
                        saved.id(),
                        "New title",
                        "New text",
                        List.of("new", "spring"),
                        0L,
                        0L,
                        null
                )
        ).orElseThrow();

        assertEquals("New title", updated.title());
        assertEquals("New text", updated.text());
        assertEquals(List.of("new", "spring"), updated.tags());
    }

    @Test
    void update_shouldReturnEmptyWhenPostDoesNotExist() {
        Optional<Post> result = postDao.update(
                999_999L,
                new Post(
                        999_999L,
                        "Title",
                        "Text",
                        List.of(),
                        0L,
                        0L,
                        null
                )
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void delete_shouldRemovePost() {
        Post saved = postDao.save(PostTestDataFactory.createPost())
                .orElseThrow();

        postDao.delete(saved.id());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM posts WHERE id = ?",
                Integer.class,
                saved.id()
        );

        assertEquals(0, count);
    }
}