package org.example.mematch.application.service;

import org.example.mematch.domain.entities.Match;
import org.example.mematch.domain.entities.Message;
import org.example.mematch.domain.entities.User;
import org.example.mematch.infrastructure.persistence.jpa.MatchRepository;
import org.example.mematch.infrastructure.persistence.jpa.MessageRepository;
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
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    private User user1;
    private User user2;
    private Match matchedMatch;
    private Match unmatchedMatch;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        user1 = User.create("user1@example.com", "user1", "hash1");
        user2 = User.create("user2@example.com", "user2", "hash2");
        matchedMatch = Match.create(user1, user2);
        matchedMatch.markAsMatched();
        unmatchedMatch = Match.create(user1, user2);
        testMessage = Message.create(matchedMatch, user1, "Hello!");
        
        // Set IDs using reflection for testing
        try {
            java.lang.reflect.Field userIdField = User.class.getDeclaredField("id");
            userIdField.setAccessible(true);
            userIdField.set(user1, 1L);
            userIdField.set(user2, 2L);
            
            java.lang.reflect.Field matchIdField = Match.class.getDeclaredField("id");
            matchIdField.setAccessible(true);
            matchIdField.set(matchedMatch, 1L);
            matchIdField.set(unmatchedMatch, 2L);
            
            java.lang.reflect.Field messageIdField = Message.class.getDeclaredField("id");
            messageIdField.setAccessible(true);
            messageIdField.set(testMessage, 1L);
        } catch (Exception e) {
            // If reflection fails, tests will need to handle null IDs
        }
    }

    @Test
    void createMessage_WhenMatchExistsAndMatched_ShouldCreateAndReturnMessage() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(matchedMatch));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

        Message result = messageService.createMessage(1L, 1L, "Hello!");

        assertNotNull(result);
        assertEquals("Hello!", result.getContent());
        assertEquals(user1, result.getSender());
        assertEquals(matchedMatch, result.getMatch());
        verify(matchRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void createMessage_WhenMatchIsNotMatched_ShouldThrowException() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(unmatchedMatch));

        assertThrows(IllegalStateException.class,
            () -> messageService.createMessage(1L, 1L, "Hello!"));
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void createMessage_WhenMatchDoesNotExist_ShouldThrowException() {
        when(matchRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> messageService.createMessage(999L, 1L, "Hello!"));
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void createMessage_WhenSenderIsNotPartOfMatch_ShouldThrowException() {
        User user3 = User.create("user3@example.com", "user3", "hash3");
        // Set ID for user3
        try {
            java.lang.reflect.Field userIdField = User.class.getDeclaredField("id");
            userIdField.setAccessible(true);
            userIdField.set(user3, 3L);
        } catch (Exception e) {
            // Ignore reflection errors
        }
        
        when(matchRepository.findById(1L)).thenReturn(Optional.of(matchedMatch));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user3));

        assertThrows(IllegalArgumentException.class,
            () -> messageService.createMessage(1L, 3L, "Hello!"));
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void createMessage_WhenSenderIsUser2_ShouldCreateMessage() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(matchedMatch));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        Message messageFromUser2 = Message.create(matchedMatch, user2, "Hi there!");
        when(messageRepository.save(any(Message.class))).thenReturn(messageFromUser2);

        Message result = messageService.createMessage(1L, 2L, "Hi there!");

        assertNotNull(result);
        assertEquals(user2, result.getSender());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void getMessagesByMatchId_ShouldReturnMessages() {
        Message message1 = Message.create(matchedMatch, user1, "Message 1");
        Message message2 = Message.create(matchedMatch, user2, "Message 2");
        when(messageRepository.findByMatchId(1L)).thenReturn(Arrays.asList(message1, message2));

        List<Message> result = messageService.getMessagesByMatchId(1L);

        assertEquals(2, result.size());
        assertTrue(result.contains(message1));
        assertTrue(result.contains(message2));
        verify(messageRepository, times(1)).findByMatchId(1L);
    }

    @Test
    void getMessageById_WhenMessageExists_ShouldReturnMessage() {
        when(messageRepository.findById(1L)).thenReturn(Optional.of(testMessage));

        Message result = messageService.getMessageById(1L);

        assertNotNull(result);
        assertEquals(testMessage, result);
        verify(messageRepository, times(1)).findById(1L);
    }

    @Test
    void getMessageById_WhenMessageDoesNotExist_ShouldThrowException() {
        when(messageRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> messageService.getMessageById(999L));
    }

    @Test
    void sendMessage_ShouldSaveAndReturnMessage() {
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

        Message result = messageService.sendMessage(testMessage);

        assertNotNull(result);
        assertEquals(testMessage, result);
        verify(messageRepository, times(1)).save(testMessage);
    }
}
