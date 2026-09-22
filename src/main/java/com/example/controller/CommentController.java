package com.example.controller;

import com.example.dto.comment.CommentRequest;
import com.example.dto.comment.CommentResponse;
import com.example.service.CommentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@CrossOrigin(origins = "http://localhost")
public class CommentController {

    private static final Logger log =
            LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentResponse> getComments(
            @PathVariable long postId
    ) {
        log.debug("Getting comment: postId={}", postId);

        return commentService.getByPostId(postId);
    }

    @GetMapping("/{commentId}")
    public CommentResponse getComment(
            @PathVariable long postId,
            @PathVariable long commentId
    ) {
        log.debug("Getting comments for postId={}", postId);

        return commentService.getById(postId, commentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(
            @PathVariable long postId,
            @Valid @RequestBody CommentRequest request
    ) {
        log.info("Creating comment for postId={}", postId);

        return commentService.create(postId, request);
    }

    @PutMapping("/{commentId}")
    public CommentResponse updateComment(
            @PathVariable long postId,
            @PathVariable long commentId,
            @Valid @RequestBody CommentRequest request
    ) {
        log.info(
                "Updating comment: postId={}, commentId={}",
                postId,
                commentId
        );

        return commentService.update(postId, commentId, request);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(
            @PathVariable long postId,
            @PathVariable long commentId
    ) {
        log.info(
                "Deleting comment: postId={}, commentId={}",
                postId,
                commentId
        );

        commentService.delete(postId, commentId);
    }
}