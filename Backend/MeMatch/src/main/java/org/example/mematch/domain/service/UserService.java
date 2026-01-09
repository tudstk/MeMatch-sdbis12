package org.example.mematch.domain.service;

import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;
import org.example.mematch.domain.valueobjects.HumourTag;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(String email, String username, String passwordHash);

    Optional<User> getById(Long id);

    List<User> getAll();

    List<User> getUsersForFeed(Long userId);

    List<User> searchUsersByUsername(String query);

    User updateProfile(Long id, String description, String imageUrl);
    
    User updateProfileDetails(Long id, Integer age, String gender, String city, String country, List<HumourTag> humourTags);
    
    User updatePreferences(Long id, String genderPreference, Integer ageMinPreference, Integer ageMaxPreference, List<HumourTag> humourTagsPreference);

    Meme postMeme(Long userId, String imageUrl, String caption);

    void removeMeme(Long userId, Long memeId);
}
