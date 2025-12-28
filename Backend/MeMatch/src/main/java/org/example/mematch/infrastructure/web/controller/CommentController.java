package org.example.mematch.infrastructure.web.controller;

import org.example.mematch.application.service.CommentServiceImpl;
import org.example.mematch.domain.entities.Comment;
import org.example.mematch.infrastructure.web.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentServiceImpl commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/meme/{memeId}/user/{userId}")
    public ResponseEntity<Comment> createComment(
            @PathVariable Long memeId,
            @PathVariable Long userId,
            @RequestBody CreateCommentRequest request) {
        try {
            Comment comment = commentService.createComment(userId, memeId, request.content);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    @GetMapping("/meme/{memeId}")
    public ResponseEntity<List<Comment>> getCommentsByMeme(@PathVariable Long memeId) {
        try {
            List<Comment> comments = commentService.getCommentsByMemeId(memeId);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getComment(@PathVariable Long commentId) {
        try {
            Comment comment = commentService.getCommentById(commentId);
            return ResponseEntity.ok(comment);
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request) {
        try {
            Comment comment = commentService.updateComment(commentId, request.content);
            return ResponseEntity.ok(comment);
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    public static class CreateCommentRequest {
        public String content;
    }

    public static class UpdateCommentRequest {
        public String content;
    }
}

