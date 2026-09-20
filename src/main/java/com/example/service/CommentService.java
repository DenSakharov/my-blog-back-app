package com.example.service;

import com.example.dao.CommentDao;
import com.example.dto.comment.CommentRequest;
import com.example.dto.comment.CommentResponse;
import com.example.dao.PostDao;
import com.example.mapper.CommentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentDao commentDao;
    private final PostDao postDao;
    private final CommentMapper commentMapper;

    public CommentService(
            CommentDao commentDao,
            PostDao postDao,
            CommentMapper commentMapper
    ) {
        this.commentDao = commentDao;
        this.postDao = postDao;
        this.commentMapper = commentMapper;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getByPostId(long postId) {
        postDao.findById(postId);

        return commentDao.findByPostId(postId)
                .stream()
                .map(commentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentResponse getById(long postId, long commentId) {
        postDao.findById(postId);

        return commentMapper.toResponse(
                commentDao.findById(postId, commentId)
        );
    }

    @Transactional
    public CommentResponse create(
            long postId,
            CommentRequest request
    ) {
        validateText(request);
        postDao.findById(postId);

        return commentMapper.toResponse(
                commentDao.create(postId, request.text())
        );
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

        validateText(request);
        postDao.findById(postId);

        return commentMapper.toResponse(
                commentDao.update(postId, commentId, request.text())
        );
    }

    @Transactional
    public void delete(long postId, long commentId) {
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid postId");
        }

        if (commentId <= 0) {
            throw new IllegalArgumentException("Invalid commentId");
        }

        postDao.findById(postId);
        commentDao.delete(postId, commentId);
    }

    private void validateText(CommentRequest request) {
        if (request == null ||
                request.text() == null ||
                request.text().isBlank()) {
            throw new IllegalArgumentException("Comment text is required");
        }
    }
}