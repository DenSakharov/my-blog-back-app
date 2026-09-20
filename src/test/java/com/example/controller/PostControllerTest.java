package com.example.controller;

import com.example.dto.post.PostCreateRequest;
import com.example.dto.post.PostListResponse;
import com.example.dto.post.PostResponse;
import com.example.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PostControllerTest {

    private MockMvc mockMvc;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = mock(PostService.class);

        PostController controller = new PostController(postService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new TestExceptionHandler())
                .build();
    }

    @Test
    void createPost_shouldReturn201AndJson() throws Exception {
        PostResponse response = new PostResponse(
                1L,
                "Java",
                "Spring text",
                List.of("java", "spring"),
                0,
                0
        );

        when(postService.create(any(PostCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Java",
                                  "text": "Spring text",
                                  "tags": ["java", "spring"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Java"))
                .andExpect(jsonPath("$.text").value("Spring text"))
                .andExpect(jsonPath("$.tags[0]").value("java"))
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));

        verify(postService).create(any(PostCreateRequest.class));
    }

    @Test
    void getPost_shouldReturn200AndJson() throws Exception {
        when(postService.getById(1L))
                .thenReturn(new PostResponse(
                        1L,
                        "Java",
                        "Text",
                        List.of("java"),
                        3,
                        2
                ));

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Java"))
                .andExpect(jsonPath("$.likesCount").value(3))
                .andExpect(jsonPath("$.commentsCount").value(2));
    }

    @Test
    void getPosts_shouldReturn200() throws Exception {
        when(postService.getPosts("java", 1, 5))
                .thenReturn(new PostListResponse(
                        List.of(),
                        false,
                        false,
                        1
                ));

        mockMvc.perform(get("/api/posts")
                        .param("search", "java")
                        .param("pageNumber", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.lastPage").value(1));
    }

    @Test
    void createPost_shouldReturn400ForMalformedJson() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "Java",
                              "text":
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(postService);
    }

    @Test
    void getPosts_shouldReturn400ForInvalidPagination() throws Exception {
        when(postService.getPosts(anyString(), eq(0), eq(5)))
                .thenThrow(new IllegalArgumentException(
                        "Invalid pagination parameters"
                ));

        mockMvc.perform(get("/api/posts")
                        .param("pageNumber", "0")
                        .param("pageSize", "5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPost_shouldReturn404WhenPostNotFound() throws Exception {
        when(postService.getById(999L))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Post not found"
                ));

        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound());
    }
}