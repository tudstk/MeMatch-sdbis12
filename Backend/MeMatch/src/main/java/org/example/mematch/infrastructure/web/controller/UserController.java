package org.example.mematch.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.mematch.application.service.UserServiceImpl;
import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;
import org.example.mematch.infrastructure.web.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management API endpoints")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        User user = userService.getById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("User with id " + id + " not found"));
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Register a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest r) {
        User user = userService.createUser(r.email, r.username, r.passwordHash);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}/profile")
    @Operation(summary = "Update user profile", description = "Update the profile information of a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updateProfile(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @RequestBody UpdateProfileRequest r) {
        User user = userService.updateProfile(id, r.description, r.profilePictureUrl);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/memes")
    @Operation(summary = "Post a meme", description = "Create a new meme post for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meme successfully posted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Meme> postMeme(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @RequestBody PostMemeRequest r) {
        Meme meme = userService.postMeme(id, r.imageUrl, r.caption);
        return ResponseEntity.status(HttpStatus.CREATED).body(meme);
    }

    @DeleteMapping("/{userId}/memes/{memeId}")
    @Operation(summary = "Delete a meme", description = "Delete a meme posted by a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Meme successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User or meme not found")
    })
    public ResponseEntity<Void> deleteMeme(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId,
            @Parameter(description = "Meme ID", required = true) @PathVariable Long memeId) {
        userService.removeMeme(userId, memeId);
        return ResponseEntity.noContent().build();
    }

    public static class CreateUserRequest {
        public String email;
        public String username;
        public String passwordHash;
    }

    public static class UpdateProfileRequest {
        public String description;
        public String profilePictureUrl;
    }

    public static class PostMemeRequest {
        public String imageUrl;
        public String caption;
    }
}
