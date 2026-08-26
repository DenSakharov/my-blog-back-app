package com.example.dto.post;

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