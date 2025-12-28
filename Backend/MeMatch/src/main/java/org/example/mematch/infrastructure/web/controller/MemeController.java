package org.example.mematch.infrastructure.web.controller;

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
    public ResponseEntity<List<Meme>> getAllMemes() {
        List<Meme> memes = memeRepository.findAll();
        return ResponseEntity.ok(memes);
    }

    @GetMapping("/{memeId}")
    public ResponseEntity<Meme> getMeme(@PathVariable Long memeId) {
        Meme meme = memeRepository.findById(memeId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("Meme with id " + memeId + " not found"));
        return ResponseEntity.ok(meme);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Meme> createMeme(@PathVariable Long userId, @RequestBody CreateMemeRequest r) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("User with id " + userId + " not found"));
        Meme meme = memeService.createMeme(user, r.imageUrl, r.caption);
        return ResponseEntity.status(HttpStatus.CREATED).body(meme);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Meme>> getMemesByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("User with id " + userId + " not found"));
        List<Meme> memes = memeService.getMemesByUser(user);
        return ResponseEntity.ok(memes);
    }

    @PutMapping("/{memeId}")
    public ResponseEntity<Meme> updateMeme(@PathVariable Long memeId, @RequestBody UpdateMemeRequest r) {
        Meme meme = memeRepository.findById(memeId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("Meme with id " + memeId + " not found"));
        meme.updateCaption(r.caption);
        Meme updatedMeme = memeRepository.save(meme);
        return ResponseEntity.ok(updatedMeme);
    }

    @DeleteMapping("/{memeId}")
    public ResponseEntity<Void> deleteMeme(@PathVariable Long memeId) {
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
