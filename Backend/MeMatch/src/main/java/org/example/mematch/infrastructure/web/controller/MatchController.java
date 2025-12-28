package org.example.mematch.infrastructure.web.controller;

import org.example.mematch.application.service.MatchServiceImpl;
import org.example.mematch.domain.entities.Match;
import org.example.mematch.domain.entities.User;
import org.example.mematch.infrastructure.persistence.jpa.MatchRepository;
import org.example.mematch.infrastructure.persistence.jpa.UserRepository;
import org.example.mematch.infrastructure.web.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchServiceImpl matchService;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    public MatchController(MatchServiceImpl matchService, 
                          UserRepository userRepository,
                          MatchRepository matchRepository) {
        this.matchService = matchService;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
    }

    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        List<Match> matches = matchRepository.findAll();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<Match> getMatch(@PathVariable Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("Match with id " + matchId + " not found"));
        return ResponseEntity.ok(match);
    }

    @PostMapping
    public ResponseEntity<Match> createMatch(@RequestBody CreateMatchRequest r) {
        User user1 = userRepository.findById(r.user1Id)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("User with id " + r.user1Id + " not found"));
        User user2 = userRepository.findById(r.user2Id)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("User with id " + r.user2Id + " not found"));
        Match match = matchService.createMatch(user1, user2);
        return ResponseEntity.status(HttpStatus.CREATED).body(match);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Match>> getMatchesForUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("User with id " + userId + " not found"));
        List<Match> matches = matchService.getMatchesForUser(user);
        return ResponseEntity.ok(matches);
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("Match with id " + matchId + " not found"));
        matchRepository.delete(match);
        return ResponseEntity.noContent().build();
    }

    public static class CreateMatchRequest {
        public Long user1Id;
        public Long user2Id;
    }
}
