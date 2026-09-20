package com.example.mapper;

import com.example.dto.comment.CommentResponse;
import com.example.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.id(),
                comment.text(),
                comment.postId()
        );
    }
}