package com.example.like;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Transactional
    public LikeResponse addLike(long postId) {
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid postId");
        }

        long likesCount = likeRepository.incrementLikes(postId);
        return new LikeResponse(postId, likesCount);
    }

    public LikeResponse getLikes(long postId) {
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid postId");
        }

        return new LikeResponse(
                postId,
                likeRepository.getLikesCount(postId)
        );
    }

    @Transactional
    public long incrementLikes(long postId) {
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid postId");
        }

        return likeRepository.incrementLikes(postId);
    }
}