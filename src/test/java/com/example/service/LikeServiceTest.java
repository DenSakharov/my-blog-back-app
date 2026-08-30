package com.example.service;

import com.example.dao.LikeDao;
import com.example.dto.like.LikeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LikeServiceTest {

    private LikeDao likeDao;
    private LikeService likeService;

    @BeforeEach
    void setUp() {
        likeDao = mock(LikeDao.class);
        likeService = new LikeService(likeDao);
    }

    @Test
    void addLike_shouldReturnUpdatedLikesCount() {
        when(likeDao.addLike(1L)).thenReturn(5L);

        LikeResponse result = likeService.addLike(1L);

        assertEquals(new LikeResponse(1L, 5L), result);
        verify(likeDao).addLike(1L);
    }

    @Test
    void addLike_shouldRejectInvalidPostId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> likeService.addLike(0L)
        );

        verifyNoInteractions(likeDao);
    }

    @Test
    void addLike_shouldRejectNegativePostId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> likeService.addLike(-1L)
        );

        verifyNoInteractions(likeDao);
    }

    @Test
    void getLikes_shouldReturnLikesCount() {
        when(likeDao.getLikesCount(1L)).thenReturn(7L);

        LikeResponse result = likeService.getLikes(1L);

        assertEquals(new LikeResponse(1L, 7L), result);
        verify(likeDao).getLikesCount(1L);
    }

    @Test
    void getLikes_shouldRejectInvalidPostId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> likeService.getLikes(0L)
        );

        verifyNoInteractions(likeDao);
    }

    @Test
    void getLikes_shouldRejectNegativePostId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> likeService.getLikes(-1L)
        );

        verifyNoInteractions(likeDao);
    }

    @Test
    void addLike_shouldPropagatePostNotFoundException() {
        when(likeDao.addLike(999L))
                .thenThrow(new IllegalArgumentException("Post not found: 999"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> likeService.addLike(999L)
        );

        assertEquals("Post not found: 999", exception.getMessage());
        verify(likeDao).addLike(999L);
    }

    @Test
    void getLikes_shouldPropagatePostNotFoundException() {
        when(likeDao.getLikesCount(999L))
                .thenThrow(new IllegalArgumentException("Post not found: 999"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> likeService.getLikes(999L)
        );

        assertEquals("Post not found: 999", exception.getMessage());
        verify(likeDao).getLikesCount(999L);
    }
}