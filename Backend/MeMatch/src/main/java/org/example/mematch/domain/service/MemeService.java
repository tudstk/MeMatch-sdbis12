package org.example.mematch.domain.service;

import org.example.mematch.domain.entities.Meme;
import org.example.mematch.domain.entities.User;

import java.util.List;

public interface MemeService {

    Meme createMeme(User user, String imageUrl, String caption);

    List<Meme> getMemesByUser(User user);

    void deleteMeme(Long memeId);
}
