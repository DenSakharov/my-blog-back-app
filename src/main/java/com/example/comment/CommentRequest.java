package com.example.comment;

public record CommentRequest(
        Long id,
        String text,
        Long postId
) {
}