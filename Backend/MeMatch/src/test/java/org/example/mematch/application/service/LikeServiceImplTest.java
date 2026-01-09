package org.example.mematch.application.service;

import org.example.mematch.domain.entities.Like;
import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;
import org.example.mematch.infrastructure.persistence.jpa.LikeRepository;
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
class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private MemeRepository memeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LikeServiceImpl likeService;

    private User testUser;
    private User otherUser;
    private Meme testMeme;
    private Meme otherMeme;
    private Like testLike;

    @BeforeEach
    void setUp() {
        testUser = User.create("test@example.com", "testuser", "hashedpassword");
        otherUser = User.create("other@example.com", "otheruser", "hash2");
        testMeme = Meme.create(testUser, "https://example.com/meme.jpg", "Funny meme");
        otherMeme = Meme.create(otherUser, "https://example.com/other.jpg", "Other meme");
        testLike = Like.create(testUser, testMeme);
        
        // Set IDs using reflection for testing
        try {
            java.lang.reflect.Field userIdField = User.class.getDeclaredField("id");
            userIdField.setAccessible(true);
            userIdField.set(testUser, 1L);
            userIdField.set(otherUser, 2L);
            
            java.lang.reflect.Field memeIdField = Meme.class.getDeclaredField("id");
            memeIdField.setAccessible(true);
            memeIdField.set(testMeme, 1L);
            memeIdField.set(otherMeme, 2L);
            
            java.lang.reflect.Field likeIdField = Like.class.getDeclaredField("id");
            likeIdField.setAccessible(true);
            likeIdField.set(testLike, 1L);
        } catch (Exception e) {
            // If reflection fails, tests will need to handle null IDs
        }
    }

    @Test
    void createLike_WhenUserAndMemeExistAndNotLiked_ShouldCreateAndReturnLike() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(memeRepository.findById(1L)).thenReturn(Optional.of(testMeme));
        when(likeRepository.findAll()).thenReturn(Arrays.asList());
        when(likeRepository.save(any(Like.class))).thenReturn(testLike);

        Like result = likeService.createLike(1L, 1L);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testMeme, result.getMeme());
        verify(userRepository, times(1)).findById(1L);
        verify(memeRepository, times(1)).findById(1L);
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void createLike_WhenUserAlreadyLiked_ShouldThrowException() {
        // hasUserLikedMeme is called first, which checks likeRepository
        // The exception is thrown before user/meme are fetched, so no need to stub those
        when(likeRepository.findAll()).thenReturn(Arrays.asList(testLike));

        assertThrows(IllegalArgumentException.class,
            () -> likeService.createLike(1L, 1L));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void createLike_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> likeService.createLike(999L, 1L));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void createLike_WhenMemeDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(memeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> likeService.createLike(1L, 999L));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void unlikeMeme_WhenLikeExists_ShouldDeleteLike() {
        when(likeRepository.findAll()).thenReturn(Arrays.asList(testLike));

        likeService.unlikeMeme(1L, 1L);

        verify(likeRepository, times(1)).findAll();
        verify(likeRepository, times(1)).delete(testLike);
    }

    @Test
    void unlikeMeme_WhenLikeDoesNotExist_ShouldThrowException() {
        when(likeRepository.findAll()).thenReturn(Arrays.asList());

        assertThrows(IllegalArgumentException.class,
            () -> likeService.unlikeMeme(1L, 1L));
        verify(likeRepository, never()).delete(any(Like.class));
    }

    @Test
    void hasUserLikedMeme_WhenLiked_ShouldReturnTrue() {
        when(likeRepository.findAll()).thenReturn(Arrays.asList(testLike));

        boolean result = likeService.hasUserLikedMeme(1L, 1L);

        assertTrue(result);
    }

    @Test
    void hasUserLikedMeme_WhenNotLiked_ShouldReturnFalse() {
        when(likeRepository.findAll()).thenReturn(Arrays.asList());

        boolean result = likeService.hasUserLikedMeme(1L, 1L);

        assertFalse(result);
    }

    @Test
    void getLikeCount_ShouldReturnCorrectCount() {
        Like like1 = Like.create(testUser, testMeme);
        Like like2 = Like.create(otherUser, testMeme);
        Like like3 = Like.create(testUser, otherMeme);

        when(likeRepository.findAll()).thenReturn(Arrays.asList(like1, like2, like3));

        long count = likeService.getLikeCount(1L);

        assertEquals(2, count);
    }

    @Test
    void getLikeCount_WhenNoLikes_ShouldReturnZero() {
        when(likeRepository.findAll()).thenReturn(Arrays.asList());

        long count = likeService.getLikeCount(1L);

        assertEquals(0, count);
    }

    @Test
    void hasUserLikedUserMemes_WhenLiked_ShouldReturnTrue() {
        // testMeme is owned by testUser (ID 1), testLike is by testUser on testMeme
        when(memeRepository.findAll()).thenReturn(Arrays.asList(testMeme, otherMeme));
        when(likeRepository.findAll()).thenReturn(Arrays.asList(testLike));

        boolean result = likeService.hasUserLikedUserMemes(1L, 1L);

        assertTrue(result);
    }

    @Test
    void hasUserLikedUserMemes_WhenNotLiked_ShouldReturnFalse() {
        when(memeRepository.findAll()).thenReturn(Arrays.asList(testMeme));
        when(likeRepository.findAll()).thenReturn(Arrays.asList());

        boolean result = likeService.hasUserLikedUserMemes(1L, 1L);

        assertFalse(result);
    }

    @Test
    void hasUserLikedUserMemes_WhenOwnerHasNoMemes_ShouldReturnFalse() {
        when(memeRepository.findAll()).thenReturn(Arrays.asList());

        boolean result = likeService.hasUserLikedUserMemes(1L, 1L);

        assertFalse(result);
    }

    @Test
    void likeMeme_ShouldSaveAndReturnLike() {
        when(likeRepository.save(any(Like.class))).thenReturn(testLike);

        Like result = likeService.likeMeme(testLike);

        assertNotNull(result);
        assertEquals(testLike, result);
        verify(likeRepository, times(1)).save(testLike);
    }
}
