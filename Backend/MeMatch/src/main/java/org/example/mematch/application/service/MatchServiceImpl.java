package org.example.mematch.application.service;

import org.example.mematch.domain.entities.Match;
import org.example.mematch.domain.entities.User;
import org.example.mematch.domain.service.MatchService;
import org.example.mematch.infrastructure.persistence.jpa.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;

    public MatchServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public Match createMatch(User user1, User user2) {
        if (checkMatchExists(user1, user2)) {
            throw new IllegalStateException("Match already exists between these users");
        }
        Match match = Match.create(user1, user2); // use factory method
        return matchRepository.save(match);
    }

    @Override
    public boolean checkMatchExists(User user1, User user2) {
        return matchRepository.existsByUser1AndUser2(user1, user2) ||
                matchRepository.existsByUser1AndUser2(user2, user1);
    }

    @Override
    public List<Match> getMatchesForUser(User user) {
        return matchRepository.findAll().stream()
                .filter(m -> m.getUser1().equals(user) || m.getUser2().equals(user))
                .toList();
    }
}
