package org.example.mematch.domain.service;

import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(String email, String username, String passwordHash);

    Optional<User> getById(Long id);

    List<User> getAll();

    User updateProfile(Long id, String description, String imageUrl);

    Meme postMeme(Long userId, String imageUrl, String caption);

    void removeMeme(Long userId, Long memeId);
}
