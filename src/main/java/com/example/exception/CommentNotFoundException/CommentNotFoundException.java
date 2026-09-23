package com.example.exception.CommentNotFoundException;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(long commentId) {
        super("Comment not found: id=" + commentId);
    }
}