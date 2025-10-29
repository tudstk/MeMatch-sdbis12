package org.example.mematch.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "meme_id")
    private Meme meme;

    protected Like() {}

    public static Like create(User user, Meme meme) {
        Like like = new Like();
        like.user = user;
        like.meme = meme;
        return like;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Meme getMeme() { return meme; }
}
