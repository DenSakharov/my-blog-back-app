package com.example.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        Long id,

        @NotBlank(message = "Текст комментария не может быть пустым")
        @Size(max = 1000, message = "Текст комментария слишком длинный")
        String text,

        Long postId
) {
}