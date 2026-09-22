package com.example.service;

import com.example.dao.CommentDao;
import com.example.dao.PostDao;
import com.example.dto.comment.CommentRequest;
import com.example.dto.comment.CommentResponse;
import com.example.mapper.CommentMapper;
import com.example.model.Comment;
import com.example.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private CommentDao commentDao;
    private PostDao postDao;
    private CommentService commentService;
    private CommentMapper  commentMapper;

    @BeforeEach
    void setUp() {
        commentDao = mock(CommentDao.class);
        postDao = mock(PostDao.class);
        commentMapper = mock(CommentMapper.class);

        commentService = new CommentService(commentDao, postDao, commentMapper);
    }

    @Test
    void create_shouldCreateComment() {
        var request = new CommentRequest(null, "Новый комментарий", 1L);
        var comment = new Comment(10L, "Новый комментарий", 1L);
        var response = new CommentResponse(10L, "Новый комментарий", 1L);

        when(commentDao.create(1L, request.text())).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(response);

        var result = commentService.create(1L, request);

        assertEquals(response, result);

        verify(postDao).findById(1L);
        verify(commentDao).create(1L, "Новый комментарий");
        verify(commentMapper).toResponse(comment);
    }

    @Test
    void getByPostId_shouldReturnComments() {
        var comment = new Comment(10L, "Комментарий", 1L);
        var response = new CommentResponse(10L, "Комментарий", 1L);

        when(commentDao.findByPostId(1L)).thenReturn(List.of(comment));
        when(commentMapper.toResponse(comment)).thenReturn(response);

        var result = commentService.getByPostId(1L);

        assertEquals(List.of(response), result);

        verify(postDao).findById(1L);
        verify(commentDao).findByPostId(1L);
        verify(commentMapper).toResponse(comment);
    }

    @Test
    void getByPostId_shouldReturnEmptyList() {
        when(commentDao.findByPostId(1L)).thenReturn(List.of());

        var result = commentService.getByPostId(1L);

        assertTrue(result.isEmpty());

        verify(postDao).findById(1L);
        verify(commentDao).findByPostId(1L);
        verifyNoInteractions(commentMapper);
    }

    @Test
    void getById_shouldReturnComment() {
        var post = new Post(
                1L,
                "Заголовок",
                "Текст",
                List.of(),
                0L,
                0L,
                null
        );

        var comment = new Comment(
                10L,
                "Комментарий",
                1L
        );

        var response = new CommentResponse(
                10L,
                "Комментарий",
                1L
        );

        when(postDao.findById(1L))
                .thenReturn(Optional.of(post));

        when(commentDao.findById(1L, 10L))
                .thenReturn(Optional.of(comment));

        when(commentMapper.toResponse(comment))
                .thenReturn(response);

        var result = commentService.getById(1L, 10L);

        assertEquals(response, result);

        verify(postDao).findById(1L);
        verify(commentDao).findById(1L, 10L);
        verify(commentMapper).toResponse(comment);
    }

    @Test
    void update_shouldUpdateComment() {
        var request = new CommentRequest(10L, "Изменённый текст", 1L);
        var comment = new Comment(10L, "Изменённый текст", 1L);
        var response = new CommentResponse(10L, "Изменённый текст", 1L);

        when(commentDao.update(1L, 10L, request.text()))
                .thenReturn(comment);
        when(commentMapper.toResponse(comment))
                .thenReturn(response);

        var result = commentService.update(1L, 10L, request);

        assertEquals(response, result);

        verify(postDao).findById(1L);
        verify(commentDao).update(1L, 10L, "Изменённый текст");
        verify(commentMapper).toResponse(comment);
    }

    @Test
    void update_shouldRejectInvalidCommentId() {
        var request = new CommentRequest(0L, "Комментарий", 1L);

        assertThrows(
                IllegalArgumentException.class,
                () -> commentService.update(1L, 0L, request)
        );

        verifyNoInteractions(postDao, commentDao, commentMapper);
    }

    @Test
    void delete_shouldNotCallDaoForInvalidPostId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> commentService.delete(0L, 1L)
        );
    }

    @Test
    void update_shouldRejectInvalidPostId() {
        var request = new CommentRequest(1L,"Комментарий", 0L);

        assertThrows(
                IllegalArgumentException.class,
                () -> commentService.update(0, 1, request)
        );

        verifyNoInteractions(postDao, commentDao);
    }

    @Test
    void delete_shouldCheckPostAndDeleteComment() {
        commentService.delete(1, 2);

        verify(postDao).findById(1);
        verify(commentDao).delete(1, 2);
    }
}