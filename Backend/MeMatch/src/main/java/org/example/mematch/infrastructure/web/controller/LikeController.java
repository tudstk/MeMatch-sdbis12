package org.example.mematch.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.mematch.application.service.LikeServiceImpl;
import org.example.mematch.domain.entities.Like;
import org.example.mematch.infrastructure.web.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@Tag(name = "Likes", description = "Like management API endpoints")
public class LikeController {

    private final LikeServiceImpl likeService;

    public LikeController(LikeServiceImpl likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/meme/{memeId}/user/{userId}")
    @Operation(summary = "Like a meme", description = "Add a like to a meme by a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meme successfully liked"),
            @ApiResponse(responseCode = "400", description = "User has already liked this meme"),
            @ApiResponse(responseCode = "404", description = "User or meme not found")
    })
    public ResponseEntity<Like> likeMeme(
            @Parameter(description = "Meme ID", required = true) @PathVariable Long memeId,
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        try {
            Like like = likeService.createLike(userId, memeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(like);
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    @DeleteMapping("/meme/{memeId}/user/{userId}")
    @Operation(summary = "Unlike a meme", description = "Remove a like from a meme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Meme successfully unliked"),
            @ApiResponse(responseCode = "404", description = "Like not found")
    })
    public ResponseEntity<Void> unlikeMeme(
            @Parameter(description = "Meme ID", required = true) @PathVariable Long memeId,
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        try {
            likeService.unlikeMeme(userId, memeId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    @GetMapping("/meme/{memeId}/user/{userId}")
    @Operation(summary = "Check like status", description = "Check if a user has liked a specific meme")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved like status")
    public ResponseEntity<LikeStatusResponse> checkLikeStatus(
            @Parameter(description = "Meme ID", required = true) @PathVariable Long memeId,
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        boolean hasLiked = likeService.hasUserLikedMeme(userId, memeId);
        LikeStatusResponse response = new LikeStatusResponse();
        response.hasLiked = hasLiked;
        return ResponseEntity.ok(response);
    }

    @GetMapping("/meme/{memeId}/count")
    @Operation(summary = "Get like count", description = "Get the total number of likes for a meme")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved like count")
    public ResponseEntity<LikeCountResponse> getLikeCount(
            @Parameter(description = "Meme ID", required = true) @PathVariable Long memeId) {
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

