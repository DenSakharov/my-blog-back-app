package com.example.service;

import com.example.dao.LikeDao;
import com.example.dto.like.LikeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    private final LikeDao likeDao;

    public LikeService(LikeDao likeDao) {
        this.likeDao = likeDao;
    }

    @Transactional
    public LikeResponse addLike(long postId) {
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid postId");
        }

        long likesCount = likeDao.addLike(postId);
        return new LikeResponse(postId, likesCount);
    }

    public LikeResponse getLikes(long postId) {
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid postId");
        }

        return new LikeResponse(
                postId,
                likeDao.getLikesCount(postId)
        );
    }
}