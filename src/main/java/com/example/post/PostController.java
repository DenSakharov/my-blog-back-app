package com.example.post;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
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

    @GetMapping("/{id}/image")
    public ResponseEntity<Void> getImage(@PathVariable long id) {
        return ResponseEntity.notFound().build();
    }
}