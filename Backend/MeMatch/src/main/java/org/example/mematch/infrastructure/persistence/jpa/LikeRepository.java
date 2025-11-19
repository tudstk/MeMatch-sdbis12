package org.example.mematch.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.mematch.domain.entities.Like;
import org.springframework.stereotype.Repository;

@Repository
public class LikeRepository extends EntityRepositoryJPA<Like, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
