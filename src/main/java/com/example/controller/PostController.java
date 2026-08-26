package com.example.controller;

import com.example.dto.post.*;
import com.example.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/api/posts", "/posts"})
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(@RequestBody PostCreateRequest request) {
        return postService.create(request);
    }

    @GetMapping
    public PostListResponse getPosts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return postService.getPosts(search, pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    public PostResponse getPost(@PathVariable long id) {
        return postService.getById(id);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable long id) {
        PostImage image = postService.getImage(id);

        MediaType mediaType = MediaType.parseMediaType(
                image.contentType()
        );

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(image.data());
    }

    @PutMapping(
            value = "/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> updateImage(
            @PathVariable long id,
            @RequestPart("image") MultipartFile image
    ) {
        postService.updateImage(id, image);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public PostResponse updatePost(
            @PathVariable long id,
            @RequestBody PostUpdateRequest request
    ) {
        return postService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePost(@PathVariable long id) {
        postService.delete(id);
    }
}