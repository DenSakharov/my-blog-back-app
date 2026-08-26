package com.example.dto.post;

import java.util.List;

public record PostCreateRequest(
        String title,
        String text,
        List<String> tags
) {
}