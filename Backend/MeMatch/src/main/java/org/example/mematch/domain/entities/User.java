package org.example.mematch.domain.entities;

import jakarta.persistence.*;
import org.example.mematch.domain.valueobjects.HumourTag;

import java.util.ArrayList;
import java.util.List;

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
    
    // Profile information
    private Integer age;
    private String gender;
    private String city;
    private String country;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_humour_tags", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "tag")
    private List<HumourTag> humourTags = new ArrayList<>();
    
    // Preferences
    private String genderPreference;
    private Integer ageMinPreference;
    private Integer ageMaxPreference;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_humour_preferences", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "tag")
    private List<HumourTag> humourTagsPreference = new ArrayList<>();

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
    
    public void updateProfile(Integer age, String gender, String city, String country, List<HumourTag> humourTags) {
        this.age = age;
        this.gender = gender;
        this.city = city;
        this.country = country;
        this.humourTags = humourTags != null ? new ArrayList<>(humourTags) : new ArrayList<>();
    }
    
    public void updatePreferences(String genderPreference, Integer ageMinPreference, Integer ageMaxPreference, List<HumourTag> humourTagsPreference) {
        this.genderPreference = genderPreference;
        this.ageMinPreference = ageMinPreference;
        this.ageMaxPreference = ageMaxPreference;
        // Clear existing collection first to ensure JPA detects the change
        this.humourTagsPreference.clear();
        if (humourTagsPreference != null && !humourTagsPreference.isEmpty()) {
            this.humourTagsPreference.addAll(humourTagsPreference);
        }
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public Integer getAge() { return age; }
    public String getGender() { return gender; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public List<HumourTag> getHumourTags() { return humourTags; }
    public String getGenderPreference() { return genderPreference; }
    public Integer getAgeMinPreference() { return ageMinPreference; }
    public Integer getAgeMaxPreference() { return ageMaxPreference; }
    public List<HumourTag> getHumourTagsPreference() { return humourTagsPreference; }
}
