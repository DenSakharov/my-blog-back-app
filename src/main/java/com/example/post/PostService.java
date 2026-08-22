package com.example.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponse create(PostCreateRequest request) {
        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        if (request.text() == null || request.text().isBlank()) {
            throw new IllegalArgumentException("Text is required");
        }

        if (request.tags() == null || request.tags().isEmpty()) {
            throw new IllegalArgumentException("Tags are required");
        }

        return postRepository.save(request);
    }
}