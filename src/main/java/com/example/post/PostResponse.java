package com.example.post;

import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String text,
        List<String> tags,
        int likesCount,
        int commentsCount
) {
}