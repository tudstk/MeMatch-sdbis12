package org.example.mematch.application.service;

import org.example.mematch.domain.entities.Match;
import org.example.mematch.domain.entities.Message;
import org.example.mematch.domain.entities.User;
import org.example.mematch.domain.service.MessageService;
import org.example.mematch.infrastructure.persistence.jpa.MatchRepository;
import org.example.mematch.infrastructure.persistence.jpa.MessageRepository;
import org.example.mematch.infrastructure.persistence.jpa.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public MessageServiceImpl(MessageRepository messageRepository,
                              MatchRepository matchRepository,
                              UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getMessagesByMatchId(Long matchId) {
        return messageRepository.findByMatchId(matchId);
    }

    public Message createMessage(Long matchId, Long senderId, String content) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));
        
        // Verify the match is actually matched (both users liked each other)
        if (!match.isMatched()) {
            throw new IllegalStateException("Cannot send messages to unmatched users");
        }
        
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Verify sender is part of the match
        if (!match.getUser1().getId().equals(senderId) && !match.getUser2().getId().equals(senderId)) {
            throw new IllegalArgumentException("Sender is not part of this match");
        }
        
        Message message = Message.create(match, sender, content);
        return messageRepository.save(message);
    }

    public Message getMessageById(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
    }
}
