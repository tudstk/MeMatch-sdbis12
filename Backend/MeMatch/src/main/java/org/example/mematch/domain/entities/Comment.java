package org.example.mematch.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "meme_id")
    private Meme meme;

    protected Comment() {}

    public static Comment create(User user, Meme meme, String content) {
        Comment comment = new Comment();
        comment.user = user;
        comment.meme = meme;
        comment.content = content;
        return comment;
    }

    public void updateContent(String content) { this.content = content; }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public User getUser() { return user; }
    public Meme getMeme() { return meme; }
}
