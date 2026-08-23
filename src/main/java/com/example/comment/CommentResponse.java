package com.example.comment;

public record CommentResponse(
        long id,
        String text,
        long postId
) {
}