package org.example.mematch.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "memes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Meme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String caption;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    public Meme() {}

    public static Meme create(User user, String imageUrl, String caption) {
        Meme meme = new Meme();
        meme.user = user;
        meme.imageUrl = imageUrl;
        meme.caption = caption;
        return meme;
    }

    public void updateCaption(String caption) { this.caption = caption; }

    public Long getId() { return id; }
    public String getCaption() { return caption; }
    public String getImageUrl() { return imageUrl; }
    public User getUser() { return user; }
}
