package com.example.exception;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(long postId) {
        super("Post not found: id=" + postId);
    }
}