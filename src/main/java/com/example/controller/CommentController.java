package com.example.controller;

import com.example.dto.comment.CommentRequest;
import com.example.dto.comment.CommentResponse;
import com.example.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@CrossOrigin(origins = "http://localhost")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentResponse> getComments(
            @PathVariable long postId
    ) {
        return commentService.getByPostId(postId);
    }

    @GetMapping("/{commentId}")
    public CommentResponse getComment(
            @PathVariable long postId,
            @PathVariable long commentId
    ) {
        return commentService.getById(postId, commentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(
            @PathVariable long postId,
            @RequestBody CommentRequest request
    ) {
        return commentService.create(postId, request);
    }

    @PutMapping("/{commentId}")
    public CommentResponse updateComment(
            @PathVariable long postId,
            @PathVariable long commentId,
            @RequestBody CommentRequest request
    ) {
        return commentService.update(postId, commentId, request);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(
            @PathVariable long postId,
            @PathVariable long commentId
    ) {
        commentService.delete(postId, commentId);
    }
}