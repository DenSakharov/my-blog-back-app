package com.example.controller;

import com.example.dto.comment.CommentRequest;
import com.example.dto.comment.CommentResponse;
import com.example.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CommentControllerTest {

    private MockMvc mockMvc;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = mock(CommentService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new CommentController(commentService))
                .setControllerAdvice(new TestExceptionHandler())
                .build();
    }

    @Test
    void getComments_shouldReturn200AndJson() throws Exception {
        when(commentService.getByPostId(1L))
                .thenReturn(List.of(
                        new CommentResponse(10L, "First comment", 1L),
                        new CommentResponse(11L, "Second comment", 1L)
                ));

        mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].text").value("First comment"))
                .andExpect(jsonPath("$[0].postId").value(1))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].text").value("Second comment"));

        verify(commentService).getByPostId(1L);
    }

    @Test
    void getComment_shouldReturn200AndJson() throws Exception {
        when(commentService.getById(1L, 10L))
                .thenReturn(new CommentResponse(
                        10L,
                        "First comment",
                        1L
                ));

        mockMvc.perform(get("/api/posts/1/comments/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.text").value("First comment"))
                .andExpect(jsonPath("$.postId").value(1));

        verify(commentService).getById(1L, 10L);
    }

    @Test
    void createComment_shouldReturn201AndJson() throws Exception {
        when(commentService.create(eq(1L), any(CommentRequest.class)))
                .thenReturn(new CommentResponse(
                        10L,
                        "Nice post",
                        1L
                ));

        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "text": "Nice post"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.text").value("Nice post"))
                .andExpect(jsonPath("$.postId").value(1));

        verify(commentService).create(
                eq(1L),
                any(CommentRequest.class)
        );
    }

    @Test
    void updateComment_shouldReturn200AndJson() throws Exception {
        when(commentService.update(
                eq(1L),
                eq(10L),
                any(CommentRequest.class)
        )).thenReturn(new CommentResponse(
                10L,
                "Updated comment",
                1L
        ));

        mockMvc.perform(put("/api/posts/1/comments/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "text": "Updated comment"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.text").value("Updated comment"))
                .andExpect(jsonPath("$.postId").value(1));

        verify(commentService).update(
                eq(1L),
                eq(10L),
                any(CommentRequest.class)
        );
    }

    @Test
    void deleteComment_shouldReturn200() throws Exception {
        doNothing().when(commentService).delete(1L, 10L);

        mockMvc.perform(delete("/api/posts/1/comments/10"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(commentService).delete(1L, 10L);
    }

    @Test
    void createComment_shouldReturn400ForMalformedJson()
            throws Exception {

        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "text":
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @Test
    void getComment_shouldReturn404WhenCommentNotFound()
            throws Exception {

        when(commentService.getById(1L, 999L))
                .thenThrow(new ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Comment not found"
                ));

        mockMvc.perform(get("/api/posts/1/comments/999"))
                .andExpect(status().isNotFound());

        verify(commentService).getById(1L, 999L);
    }

    @Test
    void createComment_shouldReturn400ForInvalidRequest()
            throws Exception {

        when(commentService.create(eq(1L), any(CommentRequest.class)))
                .thenThrow(new IllegalArgumentException(
                        "Text is required"
                ));

        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "text": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}