package com.example.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostCreateRequest(
        @NotBlank(message = "Заголовок не может быть пустым")
        @Size(max = 255, message = "Заголовок слишком длинный")
        String title,

        @NotBlank(message = "Текст поста не может быть пустым")
        @Size(max = 10000, message = "Текст поста слишком длинный")
        String text,

        List<String> tags
) {
}