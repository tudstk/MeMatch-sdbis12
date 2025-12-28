package org.example.mematch.application.service;

import org.example.mematch.domain.entities.Like;
import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;
import org.example.mematch.domain.service.LikeService;
import org.example.mematch.infrastructure.persistence.jpa.LikeRepository;
import org.example.mematch.infrastructure.persistence.jpa.MemeRepository;
import org.example.mematch.infrastructure.persistence.jpa.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final MemeRepository memeRepository;
    private final UserRepository userRepository;

    public LikeServiceImpl(LikeRepository likeRepository,
                          MemeRepository memeRepository,
                          UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.memeRepository = memeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Like likeMeme(Like like) {
        return likeRepository.save(like);
    }

    @Override
    public boolean hasUserLikedMeme(Long userId, Long memeId) {
        return likeRepository.findAll().stream()
                .anyMatch(l -> l.getUser().getId().equals(userId) && 
                              l.getMeme().getId().equals(memeId));
    }

    public Like createLike(Long userId, Long memeId) {
        if (hasUserLikedMeme(userId, memeId)) {
            throw new IllegalArgumentException("User has already liked this meme");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Meme meme = memeRepository.findById(memeId)
                .orElseThrow(() -> new IllegalArgumentException("Meme not found"));
        Like like = Like.create(user, meme);
        return likeRepository.save(like);
    }

    public void unlikeMeme(Long userId, Long memeId) {
        Like like = likeRepository.findAll().stream()
                .filter(l -> l.getUser().getId().equals(userId) && 
                            l.getMeme().getId().equals(memeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Like not found"));
        likeRepository.delete(like);
    }

    public long getLikeCount(Long memeId) {
        return likeRepository.findAll().stream()
                .filter(l -> l.getMeme().getId().equals(memeId))
                .count();
    }
}
