package org.example.mematch.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.mematch.domain.entities.Match;
import org.example.mematch.domain.entities.User;
import org.springframework.stereotype.Repository;

@Repository
public class MatchRepository extends EntityRepositoryJPA<Match, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void flush() {
        em.flush();
    }

    public boolean existsByUser1AndUser2(User u1, User u2) {
        return !em.createQuery(
                        "SELECT m FROM Match m " +
                                "WHERE (m.user1 = :u1 AND m.user2 = :u2) " +
                                "   OR (m.user1 = :u2 AND m.user2 = :u1)", Match.class)
                .setParameter("u1", u1)
                .setParameter("u2", u2)
                .getResultList()
                .isEmpty();
    }
}
