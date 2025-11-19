package org.example.mematch.application.service;

import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;
import org.example.mematch.domain.service.MemeService;
import org.example.mematch.infrastructure.persistence.jpa.MemeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemeServiceImpl implements MemeService {

    private final MemeRepository memeRepository;

    public MemeServiceImpl(MemeRepository memeRepository) {
        this.memeRepository = memeRepository;
    }

    @Override
    public Meme createMeme(User user, String imageUrl, String caption) {
        Meme meme = Meme.create(user, imageUrl, caption); // factory method
        return memeRepository.save(meme);
    }

    @Override
    public List<Meme> getMemesByUser(User user) {
        return memeRepository.findAll().stream()
                .filter(m -> m.getUser().equals(user))
                .toList();
    }

    @Override
    public void deleteMeme(Long memeId) {
        memeRepository.findById(memeId)
                .ifPresent(memeRepository::delete);
    }
}
