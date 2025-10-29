package org.example.mematch.domain.service;

import org.example.mematch.domain.entities.Match;
import org.example.mematch.domain.entities.User;

import java.util.List;

public interface MatchService {

    Match createMatch(User user1, User user2);

    boolean checkMatchExists(User user1, User user2);

    List<Match> getMatchesForUser(User user);
}
