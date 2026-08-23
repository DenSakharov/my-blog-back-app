package com.example.post;

public record PostImage(
        byte[] data,
        String contentType
) {}