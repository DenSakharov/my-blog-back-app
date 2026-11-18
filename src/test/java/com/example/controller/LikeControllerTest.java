package com.example.controller;

import com.example.dto.like.LikeResponse;
import com.example.service.LikeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LikeController.class)
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LikeService likeService;

    @Test
    void addLike_shouldReturn200AndJson() throws Exception {
        when(likeService.addLike(1L))
                .thenReturn(new LikeResponse(1L, 1));

        mockMvc.perform(post("/api/posts/1/likes"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.postId").value(1))
                .andExpect(jsonPath("$.likesCount").value(1));

        verify(likeService).addLike(1L);
    }

    @Test
    void getLikes_shouldReturn200AndJson() throws Exception {
        when(likeService.getLikes(1L))
                .thenReturn(new LikeResponse(1L, 5));

        mockMvc.perform(get("/api/posts/1/likes"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.postId").value(1))
                .andExpect(jsonPath("$.likesCount").value(5));

        verify(likeService).getLikes(1L);
    }

    @Test
    void addLike_shouldReturn404WhenPostNotFound()
            throws Exception {

        when(likeService.addLike(999L))
                .thenThrow(new ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Post not found"
                ));

        mockMvc.perform(post("/api/posts/999/likes"))
                .andExpect(status().isNotFound());

        verify(likeService).addLike(999L);
    }

    @Test
    void getLikes_shouldReturn404WhenPostNotFound()
            throws Exception {

        when(likeService.getLikes(999L))
                .thenThrow(new ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Post not found"
                ));

        mockMvc.perform(get("/api/posts/999/likes"))
                .andExpect(status().isNotFound());

        verify(likeService).getLikes(999L);
    }

    @Test
    void addLike_shouldReturn400ForInvalidPostId()
            throws Exception {

        mockMvc.perform(post("/api/posts/abc/likes"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(likeService);
    }

    @Test
    void getLikes_shouldReturn400ForInvalidPostId()
            throws Exception {

        mockMvc.perform(get("/api/posts/abc/likes"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(likeService);
    }
}