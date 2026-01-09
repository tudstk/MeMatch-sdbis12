package org.example.mematch.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.mematch.application.service.MessageServiceImpl;
import org.example.mematch.domain.entities.Message;
import org.example.mematch.infrastructure.web.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "Message and chat management API endpoints")
public class MessageController {

    private final MessageServiceImpl messageService;

    public MessageController(MessageServiceImpl messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/match/{matchId}/user/{userId}")
    @Operation(summary = "Send a message", description = "Send a message in a match conversation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message successfully sent"),
            @ApiResponse(responseCode = "400", description = "Invalid request or match not matched"),
            @ApiResponse(responseCode = "404", description = "Match or user not found")
    })
    public ResponseEntity<Message> sendMessage(
            @Parameter(description = "Match ID", required = true) @PathVariable Long matchId,
            @Parameter(description = "Sender User ID", required = true) @PathVariable Long userId,
            @RequestBody SendMessageRequest request) {
        try {
            Message message = messageService.createMessage(matchId, userId, request.content);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/match/{matchId}")
    @Operation(summary = "Get messages by match", description = "Retrieve all messages for a specific match")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved messages"),
            @ApiResponse(responseCode = "404", description = "Match not found")
    })
    public ResponseEntity<List<Message>> getMessagesByMatch(
            @Parameter(description = "Match ID", required = true) @PathVariable Long matchId) {
        try {
            List<Message> messages = messageService.getMessagesByMatchId(matchId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException("Match not found");
        }
    }

    @GetMapping("/{messageId}")
    @Operation(summary = "Get message by ID", description = "Retrieve a specific message by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message found"),
            @ApiResponse(responseCode = "404", description = "Message not found")
    })
    public ResponseEntity<Message> getMessage(
            @Parameter(description = "Message ID", required = true) @PathVariable Long messageId) {
        try {
            Message message = messageService.getMessageById(messageId);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            throw new GlobalExceptionHandler.ResourceNotFoundException(e.getMessage());
        }
    }

    public static class SendMessageRequest {
        public String content;
    }
}
