package com.example.comment;

import com.example.post.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(
            CommentRepository commentRepository,
            PostRepository postRepository
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getByPostId(long postId) {
        postRepository.findById(postId);
        return commentRepository.findByPostId(postId);
    }

    @Transactional(readOnly = true)
    public CommentResponse getById(long postId, long commentId) {
        postRepository.findById(postId);
        return commentRepository.findById(postId, commentId);
    }

    @Transactional
    public CommentResponse create(
            long postId,
            CommentRequest request
    ) {
        postRepository.findById(postId);

        return commentRepository.create(postId, request.text());
    }

    @Transactional
    public CommentResponse update(
            long postId,
            long commentId,
            CommentRequest request
    ) {
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid postId");
        }

        if (commentId <= 0) {
            throw new IllegalArgumentException("Invalid commentId");
        }

        if (request == null ||
                request.text() == null ||
                request.text().isBlank()) {
            throw new IllegalArgumentException("Comment text is required");
        }

        // Проверяем, что пост существует
        postRepository.findById(postId);

        return commentRepository.update(
                commentId,
                postId,
                request.text()
        );
    }

    @Transactional
    public void delete(long postId, long commentId) {
        postRepository.findById(postId);
        commentRepository.delete(postId, commentId);
    }
}