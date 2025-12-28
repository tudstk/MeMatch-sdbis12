package org.example.mematch.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.mematch.application.service.CommentServiceImpl;
import org.example.mematch.domain.entities.Comment;
import org.example.mematch.infrastructure.web.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "Comment management API endpoints")
public class CommentController {

    private final CommentServiceImpl commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/meme/{memeId}/user/{userId}")
    @Operation(summary = "Create a comment", description = "Add a comment to a meme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment successfully created"),
            @ApiResponse(responseCode = "404", description = "User or meme not found")
    })
    public ResponseEntity<Comment> createComment(
            @Parameter(description = "Meme ID", required = true) @PathVariable Long memeId,
            @Parameter(description = "User ID", required = true) @PathVariable Long userId,
            @RequestBody CreateCommentRequest request) {
        try {
            Comment comment = commentService.createComment(userId, memeId, request.content);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    @GetMapping("/meme/{memeId}")
    @Operation(summary = "Get comments by meme", description = "Retrieve all comments for a specific meme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comments"),
            @ApiResponse(responseCode = "404", description = "Meme not found")
    })
    public ResponseEntity<List<Comment>> getCommentsByMeme(
            @Parameter(description = "Meme ID", required = true) @PathVariable Long memeId) {
        try {
            List<Comment> comments = commentService.getCommentsByMemeId(memeId);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "Get comment by ID", description = "Retrieve a specific comment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment found"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<Comment> getComment(
            @Parameter(description = "Comment ID", required = true) @PathVariable Long commentId) {
        try {
            Comment comment = commentService.getCommentById(commentId);
            return ResponseEntity.ok(comment);
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Update comment", description = "Update the content of a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment successfully updated"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<Comment> updateComment(
            @Parameter(description = "Comment ID", required = true) @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request) {
        try {
            Comment comment = commentService.updateComment(commentId, request.content);
            return ResponseEntity.ok(comment);
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment", description = "Delete a comment from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Comment ID", required = true) @PathVariable Long commentId) {
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

