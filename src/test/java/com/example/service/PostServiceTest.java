package com.example.service;

import com.example.dao.PostDao;
import com.example.dto.post.PostCreateRequest;
import com.example.dto.post.PostResponse;
import com.example.dto.post.PostUpdateRequest;
import com.example.mapper.PostMapper;
import com.example.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostDao postDao;
    private PostService postService;
    private PostMapper postMapper;

    @BeforeEach
    void setUp() {
        postDao = mock(PostDao.class);
        postMapper = mock(PostMapper.class);

        postService = new PostService(postDao, postMapper);
    }

    @Test
    void create_shouldCreatePost() {
        var request = new PostCreateRequest(
                "Заголовок",
                "Текст",
                List.of("java")
        );

        var post = new Post(
                0L,
                "Заголовок",
                "Текст",
                List.of("java"),
                0L,
                0L,
                null
        );

        var response = mock(PostResponse.class);

        when(postDao.save(post)).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(response);

        var result = postService.create(request);

        assertSame(response, result);

        verify(postDao).save(post);
        verify(postMapper).toResponse(post);
    }

    @Test
    void getById_shouldReturnPost() {
        var post = new Post(
                1L,
                "Заголовок",
                "Текст",
                List.of("java"),
                2L,
                1L,
                null
        );

        var response = mock(PostResponse.class);

        when(postDao.findById(1L))
                .thenReturn(Optional.of(post));

        when(postMapper.toResponse(post))
                .thenReturn(response);

        var result = postService.getById(1L);

        assertSame(response, result);

        verify(postDao).findById(1L);
        verify(postMapper).toResponse(post);
    }

    @Test
    void update_shouldUpdatePost() {
        var request = new PostUpdateRequest(
                1L,
                "Новый заголовок",
                "Новый текст",
                List.of("spring")
        );

        var post = new Post(
                1L,
                "Новый заголовок",
                "Новый текст",
                List.of("spring"),
                0L,
                0L,
                null
        );

        var response = mock(PostResponse.class);

        when(postDao.update(eq(1L), any(Post.class)))
                .thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(response);

        var result = postService.update(1L, request);

        assertSame(response, result);

        verify(postDao).update(
                eq(1L),
                argThat(r ->
                        r.title().equals("Новый заголовок") &&
                                r.text().equals("Новый текст") &&
                                r.tags().equals(List.of("spring"))
                )
        );
    }

    @Test
    void update_shouldReplaceNullTagsWithEmptyList() {
        var request = new PostUpdateRequest(
                1L,
                "Заголовок",
                "Текст",
                null
        );

        var post = new Post(
                1L,
                "Заголовок",
                "Текст",
                List.of(),
                0L,
                0L,
                null
        );

        when(postDao.update(eq(1L), any(Post.class)))
                .thenReturn(post);

        when(postMapper.toResponse(post))
                .thenReturn(mock(PostResponse.class));

        postService.update(1L, request);

        verify(postDao).update(
                eq(1L),
                argThat(r -> r.tags().isEmpty())
        );
    }

    @Test
    void delete_shouldDeletePost() {
        postService.delete(1L);

        verify(postDao).delete(1L);
    }

    @Test
    void delete_shouldRejectInvalidId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> postService.delete(0L)
        );

        verifyNoInteractions(postDao);
    }

    @Test
    void create_shouldRejectNullTitle() {
        var request = new PostCreateRequest(
                null,
                "Текст",
                List.of("java")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> postService.create(request)
        );

        verifyNoInteractions(postDao);
    }

    @Test
    void create_shouldRejectNullTags() {
        var request = new PostCreateRequest(
                "Заголовок",
                "Текст",
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> postService.create(request)
        );

        verifyNoInteractions(postDao);
    }

    @Test
    void create_shouldRejectBlankTitle() {
        var request = new PostCreateRequest(
                "",
                "Текст",
                java.util.List.of("java")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> postService.create(request)
        );

        verifyNoInteractions(postDao);
    }

    @Test
    void create_shouldRejectBlankText() {
        var request = new PostCreateRequest(
                "Заголовок",
                "",
                java.util.List.of("java")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> postService.create(request)
        );

        verifyNoInteractions(postDao);
    }

    @Test
    void create_shouldRejectEmptyTags() {
        var request = new PostCreateRequest(
                "Заголовок",
                "Текст",
                java.util.List.of()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> postService.create(request)
        );

        verifyNoInteractions(postDao);
    }
}