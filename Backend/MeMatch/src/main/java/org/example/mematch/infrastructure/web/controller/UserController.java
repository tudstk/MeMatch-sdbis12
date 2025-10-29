package org.example.mematch.infrastructure.web.controller;

import org.example.mematch.application.service.UserServiceImpl;
import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;
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
    public List<User> all() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return userService.getById(id).orElseThrow();
    }

    @PostMapping
    public User create(@RequestBody CreateUserRequest r) {
        return userService.createUser(r.email, r.username, r.passwordHash);
    }

    @PutMapping("/{id}/profile")
    public User updateProfile(@PathVariable Long id, @RequestBody UpdateProfileRequest r) {
        return userService.updateProfile(id, r.description, r.profilePictureUrl);
    }

    @PostMapping("/{id}/memes")
    public Meme postMeme(@PathVariable Long id, @RequestBody PostMemeRequest r) {
        return userService.postMeme(id, r.imageUrl, r.caption);
    }

    @DeleteMapping("/{userId}/memes/{memeId}")
    public void deleteMeme(@PathVariable Long userId, @PathVariable Long memeId) {
        userService.removeMeme(userId, memeId);
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
