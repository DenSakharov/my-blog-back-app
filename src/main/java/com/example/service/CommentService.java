package com.example.service;

import com.example.dao.CommentDao;
import com.example.dto.comment.CommentRequest;
import com.example.dto.comment.CommentResponse;
import com.example.dao.PostDao;
import com.example.exception.CommentNotFoundException;
import com.example.mapper.CommentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private static final Logger log =
            LoggerFactory.getLogger(CommentService.class);

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
        validatePostId(postId);

        log.debug("Loading comments for postId={}", postId);

        postDao.findById(postId);

        return commentDao.findByPostId(postId)
                .stream()
                .map(commentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentResponse getById(long postId, long commentId) {
        validateIds(postId, commentId);

        log.debug(
                "Loading comment: postId={}, commentId={}",
                postId,
                commentId
        );

        postDao.findById(postId);

        return commentMapper.toResponse(
                commentDao.findById(postId, commentId)
                        .orElseThrow(() -> new CommentNotFoundException(commentId))
        );
    }

    @Transactional
    public CommentResponse create(
            long postId,
            CommentRequest request
    ) {
        validatePostId(postId);

        log.info("Creating comment for postId={}", postId);

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
        log.info(
                "Updating comment: postId={}, commentId={}",
                postId,
                commentId
        );

        validateIds(postId, commentId);

        postDao.findById(postId);

        return commentMapper.toResponse(
                commentDao.update(postId, commentId, request.text())
        );
    }

    @Transactional
    public void delete(long postId, long commentId) {
        log.info(
                "Deleting comment: postId={}, commentId={}",
                postId,
                commentId
        );

        validateIds(postId, commentId);

        postDao.findById(postId);
        commentDao.delete(postId, commentId);
    }

    private void validateIds(long postId, long commentId) {
        if (postId <= 0) {
            log.warn("Invalid postId={}", postId);
            throw new IllegalArgumentException("Invalid postId");
        }

        if (commentId <= 0) {
            log.warn("Invalid commentId={}", commentId);
            throw new IllegalArgumentException("Invalid commentId");
        }
    }

    private void validatePostId(long postId) {
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid postId: " + postId);
        }
    }
}