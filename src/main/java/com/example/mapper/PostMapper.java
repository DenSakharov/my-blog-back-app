package com.example.mapper;

import com.example.dto.post.PostResponse;
import com.example.model.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostResponse toResponse(Post post) {
        return new PostResponse(
                post.id(),
                post.title(),
                post.text(),
                post.tags(),
                Math.toIntExact(post.likesCount()),
                Math.toIntExact(post.commentsCount())
        );
    }
}