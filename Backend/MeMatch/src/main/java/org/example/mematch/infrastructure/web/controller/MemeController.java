package org.example.mematch.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.mematch.application.service.MemeServiceImpl;
import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;
import org.example.mematch.infrastructure.persistence.jpa.MemeRepository;
import org.example.mematch.infrastructure.persistence.jpa.UserRepository;
import org.example.mematch.infrastructure.web.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/memes")
@Tag(name = "Memes", description = "Meme management API endpoints")
public class MemeController {

    private final MemeServiceImpl memeService;
    private final UserRepository userRepository;
    private final MemeRepository memeRepository;

    public MemeController(MemeServiceImpl memeService, 
                         UserRepository userRepository,
                         MemeRepository memeRepository) {
        this.memeService = memeService;
        this.userRepository = userRepository;
        this.memeRepository = memeRepository;
    }

    @GetMapping
    @Operation(summary = "Get all memes", description = "Retrieve a list of all memes in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of memes")
    public ResponseEntity<List<Meme>> getAllMemes() {
        List<Meme> memes = memeRepository.findAll();
        return ResponseEntity.ok(memes);
    }

    @GetMapping("/{memeId}")
    @Operation(summary = "Get meme by ID", description = "Retrieve a specific meme by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meme found"),
            @ApiResponse(responseCode = "404", description = "Meme not found")
    })
    public ResponseEntity<Meme> getMeme(
            @Parameter(description = "Meme ID", required = true) @PathVariable Long memeId) {
        Meme meme = memeRepository.findById(memeId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("Meme with id " + memeId + " not found"));
        return ResponseEntity.ok(meme);
    }

    @PostMapping("/user/{userId}")
    @Operation(summary = "Create a new meme", description = "Create a new meme post for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meme successfully created"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Meme> createMeme(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId,
            @RequestBody CreateMemeRequest r) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("User with id " + userId + " not found"));
        Meme meme = memeService.createMeme(user, r.imageUrl, r.caption);
        return ResponseEntity.status(HttpStatus.CREATED).body(meme);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get memes by user", description = "Retrieve all memes posted by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's memes"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<Meme>> getMemesByUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("User with id " + userId + " not found"));
        List<Meme> memes = memeService.getMemesByUser(user);
        return ResponseEntity.ok(memes);
    }

    @PutMapping("/{memeId}")
    @Operation(summary = "Update meme", description = "Update the caption of a meme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meme successfully updated"),
            @ApiResponse(responseCode = "404", description = "Meme not found")
    })
    public ResponseEntity<Meme> updateMeme(
            @Parameter(description = "Meme ID", required = true) @PathVariable Long memeId,
            @RequestBody UpdateMemeRequest r) {
        Meme meme = memeRepository.findById(memeId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("Meme with id " + memeId + " not found"));
        meme.updateCaption(r.caption);
        Meme updatedMeme = memeRepository.save(meme);
        return ResponseEntity.ok(updatedMeme);
    }

    @DeleteMapping("/{memeId}")
    @Operation(summary = "Delete a meme", description = "Delete a meme from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Meme successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Meme not found")
    })
    public ResponseEntity<Void> deleteMeme(
            @Parameter(description = "Meme ID", required = true) @PathVariable Long memeId) {
        memeService.deleteMeme(memeId);
        return ResponseEntity.noContent().build();
    }

    public static class CreateMemeRequest {
        public String imageUrl;
        public String caption;
    }

    public static class UpdateMemeRequest {
        public String caption;
    }
}
