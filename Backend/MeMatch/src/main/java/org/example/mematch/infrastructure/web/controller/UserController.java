package org.example.mematch.infrastructure.web.controller;

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
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("User with id " + id + " not found"));
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest r) {
        User user = userService.createUser(r.email, r.username, r.passwordHash);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody UpdateProfileRequest r) {
        User user = userService.updateProfile(id, r.description, r.profilePictureUrl);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/memes")
    public ResponseEntity<Meme> postMeme(@PathVariable Long id, @RequestBody PostMemeRequest r) {
        Meme meme = userService.postMeme(id, r.imageUrl, r.caption);
        return ResponseEntity.status(HttpStatus.CREATED).body(meme);
    }

    @DeleteMapping("/{userId}/memes/{memeId}")
    public ResponseEntity<Void> deleteMeme(@PathVariable Long userId, @PathVariable Long memeId) {
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
