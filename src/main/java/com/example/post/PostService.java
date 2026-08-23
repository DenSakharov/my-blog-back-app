package com.example.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public PostListResponse getPosts(
            String search,
            int pageNumber,
            int pageSize
    ) {
        if (pageNumber < 1 || pageSize < 1) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        int total = postRepository.countPosts(search);

        int lastPage = Math.max(
                1,
                (int) Math.ceil((double) total / pageSize)
        );

        List<PostResponse> posts =
                postRepository.findPosts(search, pageNumber, pageSize);

        return new PostListResponse(
                posts,
                pageNumber > 1,
                pageNumber < lastPage,
                lastPage
        );
    }
}