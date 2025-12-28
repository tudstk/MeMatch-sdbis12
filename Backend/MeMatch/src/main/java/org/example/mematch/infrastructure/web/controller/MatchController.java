package org.example.mematch.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Matches", description = "Match management API endpoints")
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
    @Operation(summary = "Get all matches", description = "Retrieve a list of all matches in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of matches")
    public ResponseEntity<List<Match>> getAllMatches() {
        List<Match> matches = matchRepository.findAll();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/{matchId}")
    @Operation(summary = "Get match by ID", description = "Retrieve a specific match by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match found"),
            @ApiResponse(responseCode = "404", description = "Match not found")
    })
    public ResponseEntity<Match> getMatch(
            @Parameter(description = "Match ID", required = true) @PathVariable Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("Match with id " + matchId + " not found"));
        return ResponseEntity.ok(match);
    }

    @PostMapping
    @Operation(summary = "Create a new match", description = "Create a match between two users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Match successfully created"),
            @ApiResponse(responseCode = "404", description = "One or both users not found")
    })
    public ResponseEntity<Match> createMatch(@RequestBody CreateMatchRequest r) {
        User user1 = userRepository.findById(r.user1Id)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("User with id " + r.user1Id + " not found"));
        User user2 = userRepository.findById(r.user2Id)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("User with id " + r.user2Id + " not found"));
        Match match = matchService.createMatch(user1, user2);
        return ResponseEntity.status(HttpStatus.CREATED).body(match);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get matches for user", description = "Retrieve all matches for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's matches"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<Match>> getMatchesForUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("User with id " + userId + " not found"));
        List<Match> matches = matchService.getMatchesForUser(user);
        return ResponseEntity.ok(matches);
    }

    @DeleteMapping("/{matchId}")
    @Operation(summary = "Delete a match", description = "Delete a match from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Match successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Match not found")
    })
    public ResponseEntity<Void> deleteMatch(
            @Parameter(description = "Match ID", required = true) @PathVariable Long matchId) {
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
