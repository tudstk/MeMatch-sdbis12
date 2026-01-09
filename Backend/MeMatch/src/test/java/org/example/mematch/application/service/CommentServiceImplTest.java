package org.example.mematch.application.service;

import org.example.mematch.domain.entities.Comment;
import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;
import org.example.mematch.infrastructure.persistence.jpa.CommentRepository;
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
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemeRepository memeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User testUser;
    private Meme testMeme;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        testUser = User.create("test@example.com", "testuser", "hashedpassword");
        testMeme = Meme.create(testUser, "https://example.com/meme.jpg", "Funny meme");
        testComment = Comment.create(testUser, testMeme, "Great meme!");
        
        // Set IDs using reflection for testing
        try {
            java.lang.reflect.Field memeIdField = Meme.class.getDeclaredField("id");
            memeIdField.setAccessible(true);
            memeIdField.set(testMeme, 1L);
            
            java.lang.reflect.Field userIdField = User.class.getDeclaredField("id");
            userIdField.setAccessible(true);
            userIdField.set(testUser, 1L);
            
            java.lang.reflect.Field commentIdField = Comment.class.getDeclaredField("id");
            commentIdField.setAccessible(true);
            commentIdField.set(testComment, 1L);
        } catch (Exception e) {
            // If reflection fails, tests will need to handle null IDs
        }
    }

    @Test
    void createComment_WhenUserAndMemeExist_ShouldCreateAndReturnComment() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(memeRepository.findById(1L)).thenReturn(Optional.of(testMeme));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        Comment result = commentService.createComment(1L, 1L, "Great meme!");

        assertNotNull(result);
        assertEquals("Great meme!", result.getContent());
        assertEquals(testUser, result.getUser());
        assertEquals(testMeme, result.getMeme());
        verify(userRepository, times(1)).findById(1L);
        verify(memeRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> commentService.createComment(999L, 1L, "Great meme!"));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_WhenMemeDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(memeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> commentService.createComment(1L, 999L, "Great meme!"));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getCommentsByMemeId_WhenMemeExists_ShouldReturnComments() {
        Comment comment1 = Comment.create(testUser, testMeme, "Comment 1");
        Comment comment2 = Comment.create(testUser, testMeme, "Comment 2");
        Meme otherMeme = Meme.create(testUser, "https://example.com/other.jpg", "Other meme");
        Comment otherComment = Comment.create(testUser, otherMeme, "Other comment");
        
        // Set IDs for the new objects
        try {
            java.lang.reflect.Field memeIdField = Meme.class.getDeclaredField("id");
            memeIdField.setAccessible(true);
            memeIdField.set(otherMeme, 2L);
        } catch (Exception e) {
            // Ignore reflection errors
        }

        when(memeRepository.findById(1L)).thenReturn(Optional.of(testMeme));
        when(commentRepository.findAll()).thenReturn(Arrays.asList(comment1, comment2, otherComment));

        List<Comment> result = commentService.getCommentsByMemeId(1L);

        assertEquals(2, result.size());
        assertTrue(result.contains(comment1));
        assertTrue(result.contains(comment2));
        assertFalse(result.contains(otherComment));
        verify(memeRepository, times(1)).findById(1L);
    }

    @Test
    void getCommentsByMemeId_WhenMemeDoesNotExist_ShouldThrowException() {
        when(memeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> commentService.getCommentsByMemeId(999L));
    }

    @Test
    void deleteComment_WhenCommentExists_ShouldDeleteComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).delete(testComment);
    }

    @Test
    void deleteComment_WhenCommentDoesNotExist_ShouldThrowException() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> commentService.deleteComment(999L));
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void updateComment_WhenCommentExists_ShouldUpdateAndReturnComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        Comment result = commentService.updateComment(1L, "Updated comment");

        assertNotNull(result);
        assertEquals("Updated comment", result.getContent());
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void updateComment_WhenCommentDoesNotExist_ShouldThrowException() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> commentService.updateComment(999L, "Updated comment"));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getCommentById_WhenCommentExists_ShouldReturnComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        Comment result = commentService.getCommentById(1L);

        assertNotNull(result);
        assertEquals(testComment, result);
        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    void getCommentById_WhenCommentDoesNotExist_ShouldThrowException() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> commentService.getCommentById(999L));
    }

    @Test
    void addComment_ShouldSaveAndReturnComment() {
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        Comment result = commentService.addComment(testComment);

        assertNotNull(result);
        assertEquals(testComment, result);
        verify(commentRepository, times(1)).save(testComment);
    }
}
