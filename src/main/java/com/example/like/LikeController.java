package com.example.like;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@CrossOrigin(origins = "http://localhost")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public LikeResponse addLike(@PathVariable long postId) {
        return likeService.addLike(postId);
    }

    @GetMapping
    public LikeResponse getLikes(@PathVariable long postId) {
        return likeService.getLikes(postId);
    }

//    @PostMapping
//    public long incrementLikes(@PathVariable long postId) {
//        return likeService.incrementLikes(postId);
//    }
}