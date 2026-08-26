package com.example.dto.post;

import java.util.List;

public record PostListResponse(
        List<PostResponse> posts,
        boolean hasPrev,
        boolean hasNext,
        int lastPage
) {}
