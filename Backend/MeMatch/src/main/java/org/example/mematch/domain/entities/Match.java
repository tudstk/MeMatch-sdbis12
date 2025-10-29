package org.example.mematch.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user2_id")
    private User user2;

    @Column(nullable = false)
    private boolean matched;

    protected Match() {}

    public static Match create(User user1, User user2) {
        if (user1.equals(user2)) {
            throw new IllegalArgumentException("Cannot match a user with themselves");
        }
        Match match = new Match();
        match.user1 = user1;
        match.user2 = user2;
        match.matched = false;
        return match;
    }

    public void markAsMatched() {
        this.matched = true;
    }

    public Long getId() { return id; }
    public User getUser1() { return user1; }
    public User getUser2() { return user2; }
    public boolean isMatched() { return matched; }
}
