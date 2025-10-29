package org.example.mematch.infrastructure.web.controller;

import org.example.mematch.application.service.MemeServiceImpl;
import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;
import org.example.mematch.infrastructure.persistence.jpa.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memes")
public class MemeController {

    private final MemeServiceImpl memeService;
    private final UserRepository userRepository;

    public MemeController(MemeServiceImpl memeService, UserRepository userRepository) {
        this.memeService = memeService;
        this.userRepository = userRepository;
    }

    @PostMapping("/user/{userId}")
    public Meme createMeme(@PathVariable Long userId, @RequestBody CreateMemeRequest r) {
        User user = userRepository.findById(userId).orElseThrow();
        return memeService.createMeme(user, r.imageUrl, r.caption);
    }

    @GetMapping("/user/{userId}")
    public List<Meme> getMemesByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return memeService.getMemesByUser(user);
    }

    @DeleteMapping("/{memeId}")
    public void deleteMeme(@PathVariable Long memeId) {
        memeService.deleteMeme(memeId);
    }

    public static class CreateMemeRequest {
        public String imageUrl;
        public String caption;
    }
}
