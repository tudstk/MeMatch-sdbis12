package org.example.mematch.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.mematch.domain.entities.Match;
import org.example.mematch.domain.entities.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageRepository extends EntityRepositoryJPA<Message, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Message> findByMatch(Match match) {
        return em.createQuery(
                "SELECT m FROM Message m WHERE m.match = :match ORDER BY m.createdAt ASC", Message.class)
                .setParameter("match", match)
                .getResultList();
    }

    public List<Message> findByMatchId(Long matchId) {
        return em.createQuery(
                "SELECT m FROM Message m WHERE m.match.id = :matchId ORDER BY m.createdAt ASC", Message.class)
                .setParameter("matchId", matchId)
                .getResultList();
    }
}
