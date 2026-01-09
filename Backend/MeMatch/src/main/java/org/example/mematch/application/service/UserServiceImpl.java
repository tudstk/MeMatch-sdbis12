package org.example.mematch.application.service;

import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.Match;
import org.example.mematch.domain.entities.User;
import org.example.mematch.domain.service.UserService;
import org.example.mematch.domain.valueobjects.HumourTag;
import org.example.mematch.infrastructure.persistence.jpa.UserRepository;
import org.example.mematch.infrastructure.persistence.jpa.MemeRepository;
import org.example.mematch.infrastructure.persistence.jpa.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MemeRepository memeRepository;
    private final MatchRepository matchRepository;

    public UserServiceImpl(UserRepository userRepository, MemeRepository memeRepository, MatchRepository matchRepository) {
        this.userRepository = userRepository;
        this.memeRepository = memeRepository;
        this.matchRepository = matchRepository;
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
    public List<User> getUsersForFeed(Long userId) {
        // Get all users
        List<User> allUsers = userRepository.findAll();
        
        // Get all matches where current user is involved and matched=true
        List<Match> matchedMatches = matchRepository.findAll().stream()
                .filter(m -> m.isMatched() && 
                           (m.getUser1().getId().equals(userId) || m.getUser2().getId().equals(userId)))
                .toList();
        
        // Collect IDs of users that are matched with current user (exclude from feed)
        Set<Long> matchedUserIds = matchedMatches.stream()
                .map(m -> {
                    if (m.getUser1().getId().equals(userId)) {
                        return m.getUser2().getId();
                    } else {
                        return m.getUser1().getId();
                    }
                })
                .collect(Collectors.toSet());
        
        // Filter: exclude current user and matched users
        return allUsers.stream()
                .filter(u -> !u.getId().equals(userId) && !matchedUserIds.contains(u.getId()))
                .toList();
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
    public User updateProfileDetails(Long id, Integer age, String gender, String city, String country, List<HumourTag> humourTags) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.updateProfile(age, gender, city, country, humourTags);
        return userRepository.save(user);
    }
    
    @Override
    public User updatePreferences(Long id, String genderPreference, Integer ageMinPreference, Integer ageMaxPreference, List<HumourTag> humourTagsPreference) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.updatePreferences(genderPreference, ageMinPreference, ageMaxPreference, humourTagsPreference);
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
