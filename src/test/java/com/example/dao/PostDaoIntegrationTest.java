package com.example.dao;

import com.example.config.TestDatabaseConfig;
import com.example.fixture.PostTestDataFactory;
import com.example.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Sql(
        scripts = "/schema.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
@SpringJUnitConfig(TestDatabaseConfig.class)
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

        Post saved = postDao.save(postSaveModel);

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

        Post saved = postDao.save(postSaveModel);

        Optional<Post> foundOptional =
                postDao.findById(saved.id());

        assertTrue(foundOptional.isPresent());

        Post found = foundOptional.get();

        assertEquals(saved.id(), found.id());
        assertEquals("Java", found.title());
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
        Post saved = postDao.save(PostTestDataFactory.createPost());

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
        );

        assertEquals("New title", updated.title());
        assertEquals("New text", updated.text());
        assertEquals(List.of("new", "spring"), updated.tags());
    }

    @Test
    void delete_shouldRemovePost() {
        Post saved = postDao.save(PostTestDataFactory.createPost());

        postDao.delete(saved.id());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM posts WHERE id = ?",
                Integer.class,
                saved.id()
        );

        assertEquals(0, count);
    }
}