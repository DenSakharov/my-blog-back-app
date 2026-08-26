package com.example.dto.post;

public record PostImage(
        byte[] data,
        String contentType
) {}