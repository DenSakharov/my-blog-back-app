package com.example.post;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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

    public PostResponse getById(long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public PostResponse update(long id, PostUpdateRequest request) {
        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        if (request.text() == null || request.text().isBlank()) {
            throw new IllegalArgumentException("Text is required");
        }

        List<String> tags = request.tags() == null
                ? List.of()
                : request.tags();

        PostUpdateRequest updateRequest = new PostUpdateRequest(
                request.id(),
                request.title(),
                request.text(),
                tags
        );

        return postRepository.update(id, updateRequest);
    }

    @Transactional
    public void updateImage(long postId, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image is required");
        }

        String contentType = image.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        try {
            int updatedRows = postRepository.updateImage(
                    postId,
                    image.getBytes(),
                    contentType
            );

            if (updatedRows == 0) {
                throw new IllegalArgumentException(
                        "Post not found: " + postId
                );
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read image", e);
        }
    }

    public PostImage getImage(long postId) {
        return postRepository.findImageByPostId(postId)
                .filter(image ->
                        image.data() != null &&
                                image.data().length > 0 &&
                                image.contentType() != null &&
                                !image.contentType().isBlank()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Image not found"
                        ));
    }

    @Transactional
    public void delete(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid post id");
        }

        postRepository.delete(id);
    }
}