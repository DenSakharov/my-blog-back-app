package com.example.dto.comment;

public record CommentRequest(
        Long id,
        String text,
        Long postId
) {
}