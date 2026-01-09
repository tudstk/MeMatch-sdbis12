package org.example.mematch.application.service;

import org.example.mematch.domain.entities.Match;
import org.example.mematch.domain.entities.User;
import org.example.mematch.infrastructure.persistence.jpa.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceImplTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchServiceImpl matchService;

    private User user1;
    private User user2;
    private User user3;
    private Match match12;
    private Match match13;

    @BeforeEach
    void setUp() {
        user1 = User.create("user1@example.com", "user1", "hash1");
        user2 = User.create("user2@example.com", "user2", "hash2");
        user3 = User.create("user3@example.com", "user3", "hash3");
        
        // Set IDs using reflection for testing
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user1, 1L);
            idField.set(user2, 2L);
            idField.set(user3, 3L);
        } catch (Exception e) {
            // If reflection fails, tests will use object equality
        }
        
        match12 = Match.create(user1, user2);
        match13 = Match.create(user1, user3);
    }

    @Test
    void createMatch_WhenNoExistingMatch_ShouldCreateAndReturnMatch() {
        when(matchRepository.existsByUser1AndUser2(user1, user2)).thenReturn(false);
        when(matchRepository.existsByUser1AndUser2(user2, user1)).thenReturn(false);
        when(matchRepository.save(any(Match.class))).thenReturn(match12);

        Match result = matchService.createMatch(user1, user2);

        assertNotNull(result);
        assertEquals(user1, result.getUser1());
        assertEquals(user2, result.getUser2());
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test
    void createMatch_WhenMatchAlreadyExists_ShouldThrowException() {
        when(matchRepository.existsByUser1AndUser2(user1, user2)).thenReturn(true);

        assertThrows(IllegalStateException.class,
            () -> matchService.createMatch(user1, user2));
        
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void createMatch_WhenReverseMatchExists_ShouldThrowException() {
        when(matchRepository.existsByUser1AndUser2(user1, user2)).thenReturn(false);
        when(matchRepository.existsByUser1AndUser2(user2, user1)).thenReturn(true);

        assertThrows(IllegalStateException.class,
            () -> matchService.createMatch(user1, user2));
        
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void checkMatchExists_WhenMatchExists_ShouldReturnTrue() {
        when(matchRepository.existsByUser1AndUser2(user1, user2)).thenReturn(true);

        boolean result = matchService.checkMatchExists(user1, user2);

        assertTrue(result);
    }

    @Test
    void checkMatchExists_WhenReverseMatchExists_ShouldReturnTrue() {
        when(matchRepository.existsByUser1AndUser2(user1, user2)).thenReturn(false);
        when(matchRepository.existsByUser1AndUser2(user2, user1)).thenReturn(true);

        boolean result = matchService.checkMatchExists(user1, user2);

        assertTrue(result);
    }

    @Test
    void checkMatchExists_WhenNoMatchExists_ShouldReturnFalse() {
        when(matchRepository.existsByUser1AndUser2(user1, user2)).thenReturn(false);
        when(matchRepository.existsByUser1AndUser2(user2, user1)).thenReturn(false);

        boolean result = matchService.checkMatchExists(user1, user2);

        assertFalse(result);
    }

    @Test
    void getMatchesForUser_ShouldReturnAllMatchesForUser() {
        // Mark matches as matched since getMatchesForUser only returns matched=true
        match12.markAsMatched();
        match13.markAsMatched();
        Match match23 = Match.create(user2, user3);
        match23.markAsMatched();
        when(matchRepository.findAll()).thenReturn(Arrays.asList(match12, match13, match23));

        List<Match> result = matchService.getMatchesForUser(user1);

        assertEquals(2, result.size());
        assertTrue(result.contains(match12));
        assertTrue(result.contains(match13));
        assertFalse(result.contains(match23));
    }

    @Test
    void getMatchesForUser_WhenUserIsUser2_ShouldReturnMatches() {
        // Mark match as matched since getMatchesForUser only returns matched=true
        match12.markAsMatched();
        when(matchRepository.findAll()).thenReturn(Arrays.asList(match12, match13));

        List<Match> result = matchService.getMatchesForUser(user2);

        assertEquals(1, result.size());
        assertTrue(result.contains(match12));
    }

    @Test
    void getMatchesForUser_WhenNoMatches_ShouldReturnEmptyList() {
        when(matchRepository.findAll()).thenReturn(Arrays.asList());

        List<Match> result = matchService.getMatchesForUser(user1);

        assertTrue(result.isEmpty());
    }

    @Test
    void likeUser_WhenNoExistingMatch_ShouldCreateOneWayLike() {
        when(matchRepository.findAll()).thenReturn(Arrays.asList());
        when(matchRepository.save(any(Match.class))).thenReturn(match12);

        Match result = matchService.likeUser(user1, user2);

        assertNotNull(result);
        assertFalse(result.isMatched());
        assertEquals(user1, result.getUser1());
        assertEquals(user2, result.getUser2());
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test
    void likeUser_WhenReverseLikeExists_ShouldMarkAsMatched() {
        Match reverseMatch = Match.create(user2, user1);
        when(matchRepository.findAll()).thenReturn(Arrays.asList(reverseMatch));
        when(matchRepository.save(any(Match.class))).thenReturn(reverseMatch);
        doNothing().when(matchRepository).flush();

        Match result = matchService.likeUser(user1, user2);

        assertNotNull(result);
        assertTrue(result.isMatched());
        verify(matchRepository, times(1)).save(reverseMatch);
        verify(matchRepository, times(1)).flush();
    }

    @Test
    void likeUser_WhenSameDirectionLikeExists_ShouldReturnExisting() {
        when(matchRepository.findAll()).thenReturn(Arrays.asList(match12));

        Match result = matchService.likeUser(user1, user2);

        assertNotNull(result);
        assertEquals(match12, result);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void likeUser_WhenAlreadyMatched_ShouldReturnExisting() {
        match12.markAsMatched();
        when(matchRepository.findAll()).thenReturn(Arrays.asList(match12));

        Match result = matchService.likeUser(user1, user2);

        assertNotNull(result);
        assertTrue(result.isMatched());
        assertEquals(match12, result);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void hasUserLikedUser_WhenLiked_ShouldReturnTrue() {
        when(matchRepository.findAll()).thenReturn(Arrays.asList(match12));

        boolean result = matchService.hasUserLikedUser(user1, user2);

        assertTrue(result);
    }

    @Test
    void hasUserLikedUser_WhenNotLiked_ShouldReturnFalse() {
        when(matchRepository.findAll()).thenReturn(Arrays.asList());

        boolean result = matchService.hasUserLikedUser(user1, user2);

        assertFalse(result);
    }

    @Test
    void areUsersMatched_WhenMatched_ShouldReturnTrue() {
        match12.markAsMatched();
        when(matchRepository.findAll()).thenReturn(Arrays.asList(match12));

        boolean result = matchService.areUsersMatched(user1, user2);

        assertTrue(result);
    }

    @Test
    void areUsersMatched_WhenNotMatched_ShouldReturnFalse() {
        when(matchRepository.findAll()).thenReturn(Arrays.asList(match12));

        boolean result = matchService.areUsersMatched(user1, user2);

        assertFalse(result);
    }
}