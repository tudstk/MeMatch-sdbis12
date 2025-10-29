package org.example.mematch.infrastructure.web.controller;

import org.example.mematch.application.service.MatchServiceImpl;
import org.example.mematch.domain.entities.Match;
import org.example.mematch.domain.entities.User;
import org.example.mematch.infrastructure.persistence.jpa.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchServiceImpl matchService;
    private final UserRepository userRepository;

    public MatchController(MatchServiceImpl matchService, UserRepository userRepository) {
        this.matchService = matchService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Match createMatch(@RequestBody CreateMatchRequest r) {
        User user1 = userRepository.findById(r.user1Id).orElseThrow();
        User user2 = userRepository.findById(r.user2Id).orElseThrow();
        return matchService.createMatch(user1, user2);
    }

    @GetMapping("/user/{userId}")
    public List<Match> getMatchesForUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return matchService.getMatchesForUser(user);
    }

    public static class CreateMatchRequest {
        public Long user1Id;
        public Long user2Id;
    }
}
