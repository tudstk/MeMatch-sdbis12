package org.example.mematch.application.service;

import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;
import org.example.mematch.domain.service.UserService;
import org.example.mematch.infrastructure.persistence.jpa.UserRepository;
import org.example.mematch.infrastructure.persistence.jpa.MemeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MemeRepository memeRepository;

    public UserServiceImpl(UserRepository userRepository, MemeRepository memeRepository) {
        this.userRepository = userRepository;
        this.memeRepository = memeRepository;
    }

    @Override
    public User createUser(String email, String username, String passwordHash) {
        User user = User.create(email, username, passwordHash);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User updateProfile(Long id, String description, String imageUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.updateDescription(description);
        user.updateImage(imageUrl);
        return userRepository.save(user);
    }

    @Override
    public Meme postMeme(Long userId, String imageUrl, String caption) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Meme meme = Meme.create(user, imageUrl, caption); // assume Meme has a similar factory
        return memeRepository.save(meme);
    }

    @Override
    public void removeMeme(Long userId, Long memeId) {
        Meme meme = memeRepository.findById(memeId)
                .orElseThrow(() -> new IllegalArgumentException("Meme not found"));
        if (!meme.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User cannot delete someone else's meme");
        }
        memeRepository.delete(meme);
    }
}
