package org.example.mematch.application.service;

import org.example.mematch.domain.entities.Match;
import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;
import org.example.mematch.infrastructure.persistence.jpa.MatchRepository;
import org.example.mematch.infrastructure.persistence.jpa.MemeRepository;
import org.example.mematch.infrastructure.persistence.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MemeRepository memeRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Meme testMeme;

    @BeforeEach
    void setUp() {
        testUser = User.create("test@example.com", "testuser", "hashedpassword");
        testMeme = Meme.create(testUser, "https://example.com/meme.jpg", "Funny meme");
    }

    @Test
    void createUser_ShouldReturnSavedUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser("test@example.com", "testuser", "hashedpassword");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void getAll_ShouldReturnAllUsers() {
        User user2 = User.create("test2@example.com", "testuser2", "hash2");
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<User> result = userService.getAll();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateProfile_WhenUserExists_ShouldUpdateAndReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateProfile(1L, "New bio", "https://example.com/avatar.jpg");

        assertNotNull(result);
        assertEquals("New bio", result.getDescription());
        assertEquals("https://example.com/avatar.jpg", result.getImageUrl());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateProfile_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
            () -> userService.updateProfile(999L, "New bio", "https://example.com/avatar.jpg"));
    }

    @Test
    void postMeme_WhenUserExists_ShouldCreateAndReturnMeme() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(memeRepository.save(any(Meme.class))).thenReturn(testMeme);

        Meme result = userService.postMeme(1L, "https://example.com/meme.jpg", "Funny meme");

        assertNotNull(result);
        assertEquals("Funny meme", result.getCaption());
        assertEquals("https://example.com/meme.jpg", result.getImageUrl());
        verify(userRepository, times(1)).findById(1L);
        verify(memeRepository, times(1)).save(any(Meme.class));
    }

    @Test
    void postMeme_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> userService.postMeme(999L, "https://example.com/meme.jpg", "Funny meme"));
    }

    @Test
    void removeMeme_WhenMemeDoesNotExist_ShouldThrowException() {
        when(memeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> userService.removeMeme(1L, 999L));
    }

    @Test
    void searchUsersByUsername_WhenQueryMatches_ShouldReturnMatchingUsers() {
        User user1 = User.create("user1@example.com", "john", "hash1");
        User user2 = User.create("user2@example.com", "jane", "hash2");
        User user3 = User.create("user3@example.com", "bob", "hash3");
        when(userRepository.searchByUsername("jo")).thenReturn(Arrays.asList(user1, user2));

        List<User> result = userService.searchUsersByUsername("jo");

        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
        assertFalse(result.contains(user3));
        verify(userRepository, times(1)).searchByUsername("jo");
    }

    @Test
    void searchUsersByUsername_WhenQueryIsEmpty_ShouldReturnEmptyList() {
        List<User> result = userService.searchUsersByUsername("");

        assertTrue(result.isEmpty());
        verify(userRepository, never()).searchByUsername(anyString());
    }

    @Test
    void searchUsersByUsername_WhenQueryIsNull_ShouldReturnEmptyList() {
        List<User> result = userService.searchUsersByUsername(null);

        assertTrue(result.isEmpty());
        verify(userRepository, never()).searchByUsername(anyString());
    }

    @Test
    void updateProfileDetails_WhenUserExists_ShouldUpdateAndReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateProfileDetails(1L, 25, "Male", "New York", "USA", null);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateProfileDetails_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> userService.updateProfileDetails(999L, 25, "Male", "New York", "USA", null));
    }

    @Test
    void updatePreferences_WhenUserExists_ShouldUpdateAndReturnUser() {
        // updatePreferences calls findById twice - once to get the user, once to verify after save
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(userRepository).flush();

        User result = userService.updatePreferences(1L, "Female", 20, 30, null);

        assertNotNull(result);
        verify(userRepository, times(2)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
        verify(userRepository, times(1)).flush();
    }

    @Test
    void updatePreferences_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> userService.updatePreferences(999L, "Female", 20, 30, null));
    }
}