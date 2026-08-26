package com.example.dto.post;

import java.util.List;

public record PostUpdateRequest(
        Long id,
        String title,
        String text,
        List<String> tags
) {
}