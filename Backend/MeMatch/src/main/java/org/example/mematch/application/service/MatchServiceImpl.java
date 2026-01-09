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

    /**
     * Like a user (swipe right). Creates a one-way like or updates to a match if mutual.
     * @param liker The user who is liking
     * @param liked The user being liked
     * @return The match record (matched=true if mutual, false if one-way)
     */
    public Match likeUser(User liker, User liked) {
        // Check if any match already exists between these users (in any direction)
        // Compare by ID to avoid issues with entity equality
        Long likerId = liker.getId();
        Long likedId = liked.getId();
        
        var existingMatch = matchRepository.findAll().stream()
                .filter(m -> (m.getUser1().getId().equals(likerId) && m.getUser2().getId().equals(likedId)) ||
                            (m.getUser2().getId().equals(likerId) && m.getUser1().getId().equals(likedId)))
                .findFirst();

        if (existingMatch.isPresent()) {
            Match match = existingMatch.get();
            // If already matched, return as is
            if (match.isMatched()) {
                return match;
            }
            // Check if this is a reverse like (mutual)
            // The existing match could be:
            // - user1=liker, user2=liked (same direction - user already liked, return existing)
            // - user1=liked, user2=liker (reverse direction - mutual like! update to matched)
            boolean isReverseLike = (match.getUser1().getId().equals(likedId) && match.getUser2().getId().equals(likerId));
            
            if (isReverseLike) {
                // Mutual like - update to matched
                match.markAsMatched();
                Match saved = matchRepository.save(match);
                matchRepository.flush(); // Explicitly flush to ensure update is persisted
                System.out.println("✅ MATCH CREATED: User " + liker.getId() + " ↔ User " + liked.getId() + " (matched=true)");
                return saved;
            } else {
                // Same direction - user already liked this person
                System.out.println("⚠️ User " + liker.getId() + " already liked User " + liked.getId() + " (same direction)");
            }
            // Same direction like - user is trying to like again, return existing
            return match;
        }

        // No existing match - create new one-way like with matched=false
        Match match = Match.create(liker, liked);
        Match saved = matchRepository.save(match);
        System.out.println("💙 ONE-WAY LIKE: User " + liker.getId() + " → User " + liked.getId() + " (matched=false)");
        return saved;
    }

    /**
     * Check if a user has liked another user
     * @param liker The user who might have liked
     * @param liked The user who might be liked
     * @return true if liker has liked liked
     */
    public boolean hasUserLikedUser(User liker, User liked) {
        return matchRepository.findAll().stream()
                .anyMatch(m -> (m.getUser1().equals(liker) && m.getUser2().equals(liked)) ||
                              (m.getUser2().equals(liker) && m.getUser1().equals(liked)));
    }

    /**
     * Check if two users have matched (both liked each other)
     * @param user1 First user
     * @param user2 Second user
     * @return true if they have matched
     */
    public boolean areUsersMatched(User user1, User user2) {
        return matchRepository.findAll().stream()
                .anyMatch(m -> ((m.getUser1().equals(user1) && m.getUser2().equals(user2)) ||
                               (m.getUser2().equals(user1) && m.getUser1().equals(user2))) &&
                              m.isMatched());
    }

    @Override
    public boolean checkMatchExists(User user1, User user2) {
        return matchRepository.existsByUser1AndUser2(user1, user2) ||
                matchRepository.existsByUser1AndUser2(user2, user1);
    }

    @Override
    public List<Match> getMatchesForUser(User user) {
        return matchRepository.findAll().stream()
                .filter(m -> (m.getUser1().equals(user) || m.getUser2().equals(user)) && m.isMatched())
                .toList();
    }
}
