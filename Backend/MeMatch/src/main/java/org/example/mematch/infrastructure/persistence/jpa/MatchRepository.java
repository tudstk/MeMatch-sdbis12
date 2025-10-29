package org.example.mematch.infrastructure.persistence.jpa;

import org.example.mematch.domain.entities.Match;
import org.example.mematch.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    boolean existsByUser1AndUser2(User user1, User user2);
}
