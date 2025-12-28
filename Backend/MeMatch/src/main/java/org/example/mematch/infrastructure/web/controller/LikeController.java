package org.example.mematch.infrastructure.web.controller;

import org.example.mematch.application.service.LikeServiceImpl;
import org.example.mematch.domain.entities.Like;
import org.example.mematch.infrastructure.web.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeServiceImpl likeService;

    public LikeController(LikeServiceImpl likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/meme/{memeId}/user/{userId}")
    public ResponseEntity<Like> likeMeme(
            @PathVariable Long memeId,
            @PathVariable Long userId) {
        try {
            Like like = likeService.createLike(userId, memeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(like);
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    @DeleteMapping("/meme/{memeId}/user/{userId}")
    public ResponseEntity<Void> unlikeMeme(
            @PathVariable Long memeId,
            @PathVariable Long userId) {
        try {
            likeService.unlikeMeme(userId, memeId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    @GetMapping("/meme/{memeId}/user/{userId}")
    public ResponseEntity<LikeStatusResponse> checkLikeStatus(
            @PathVariable Long memeId,
            @PathVariable Long userId) {
        boolean hasLiked = likeService.hasUserLikedMeme(userId, memeId);
        LikeStatusResponse response = new LikeStatusResponse();
        response.hasLiked = hasLiked;
        return ResponseEntity.ok(response);
    }

    @GetMapping("/meme/{memeId}/count")
    public ResponseEntity<LikeCountResponse> getLikeCount(@PathVariable Long memeId) {
        long count = likeService.getLikeCount(memeId);
        LikeCountResponse response = new LikeCountResponse();
        response.count = count;
        return ResponseEntity.ok(response);
    }

    public static class LikeStatusResponse {
        public boolean hasLiked;
    }

    public static class LikeCountResponse {
        public long count;
    }
}

