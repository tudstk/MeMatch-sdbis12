package org.example.mematch.application.service;

import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;
import org.example.mematch.infrastructure.persistence.jpa.MemeRepository;
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
class MemeServiceImplTest {

    @Mock
    private MemeRepository memeRepository;

    @InjectMocks
    private MemeServiceImpl memeService;

    private User testUser;
    private User otherUser;
    private Meme testMeme1;
    private Meme testMeme2;
    private Meme otherMeme;

    @BeforeEach
    void setUp() {
        testUser = User.create("test@example.com", "testuser", "hashedpassword");
        otherUser = User.create("other@example.com", "otheruser", "hash2");
        
        testMeme1 = Meme.create(testUser, "https://example.com/meme1.jpg", "First meme");
        testMeme2 = Meme.create(testUser, "https://example.com/meme2.jpg", "Second meme");
        otherMeme = Meme.create(otherUser, "https://example.com/other.jpg", "Other's meme");
    }

    @Test
    void createMeme_ShouldReturnSavedMeme() {
        when(memeRepository.save(any(Meme.class))).thenReturn(testMeme1);

        Meme result = memeService.createMeme(testUser, "https://example.com/meme1.jpg", "First meme");

        assertNotNull(result);
        assertEquals("First meme", result.getCaption());
        assertEquals("https://example.com/meme1.jpg", result.getImageUrl());
        assertEquals(testUser, result.getUser());
        verify(memeRepository, times(1)).save(any(Meme.class));
    }

    @Test
    void getMemesByUser_ShouldReturnOnlyUserMemes() {
        when(memeRepository.findAll()).thenReturn(Arrays.asList(testMeme1, testMeme2, otherMeme));

        List<Meme> result = memeService.getMemesByUser(testUser);

        assertEquals(2, result.size());
        assertTrue(result.contains(testMeme1));
        assertTrue(result.contains(testMeme2));
        assertFalse(result.contains(otherMeme));
        verify(memeRepository, times(1)).findAll();
    }

    @Test
    void getMemesByUser_WhenNoMemes_ShouldReturnEmptyList() {
        when(memeRepository.findAll()).thenReturn(Arrays.asList());

        List<Meme> result = memeService.getMemesByUser(testUser);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteMeme_ShouldLoadAndDeleteMeme() {
        Long memeId = 1L;
        Meme meme = new Meme();

        when(memeRepository.findById(memeId)).thenReturn(Optional.of(meme));

        memeService.deleteMeme(memeId);

        verify(memeRepository, times(1)).findById(memeId);
        verify(memeRepository, times(1)).delete(meme);
    }


    @Test
    void createMeme_WithNullCaption_ShouldStillCreateMeme() {
        Meme memeWithoutCaption = Meme.create(testUser, "https://example.com/meme.jpg", null);
        when(memeRepository.save(any(Meme.class))).thenReturn(memeWithoutCaption);

        Meme result = memeService.createMeme(testUser, "https://example.com/meme.jpg", null);

        assertNotNull(result);
        assertNull(result.getCaption());
        assertEquals("https://example.com/meme.jpg", result.getImageUrl());
    }
}