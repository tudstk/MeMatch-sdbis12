package org.example.mematch.domain.service;

import org.example.mematch.domain.entities.Message;

import java.util.List;

public interface MessageService {
    Message sendMessage(Message message);
    List<Message> getMessagesByMatchId(Long matchId);
}
