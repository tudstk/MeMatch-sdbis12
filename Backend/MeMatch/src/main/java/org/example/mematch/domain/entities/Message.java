package org.example.mematch.domain.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Message() {}

    public static Message create(Match match, User sender, String content) {
        Message message = new Message();
        message.match = match;
        message.sender = sender;
        message.content = content;
        message.createdAt = LocalDateTime.now();
        return message;
    }

    public Long getId() { return id; }
    public Match getMatch() { return match; }
    public User getSender() { return sender; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
