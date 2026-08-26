package com.example.model;

public record Comment(
        long id,
        String text,
        long postId
) {
}