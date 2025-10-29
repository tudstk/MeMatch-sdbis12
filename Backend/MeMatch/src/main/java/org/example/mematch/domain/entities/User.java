package org.example.mematch.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "username")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    private String description;
    private String imageUrl;

    protected User() {}

    public static User create(String email, String username, String passwordHash) {
        User user = new User();
        user.email = email;
        user.username = username;
        user.passwordHash = passwordHash;
        return user;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
}
