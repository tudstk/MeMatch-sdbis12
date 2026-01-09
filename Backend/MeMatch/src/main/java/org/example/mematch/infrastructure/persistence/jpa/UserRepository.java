package org.example.mematch.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.mematch.domain.entities.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends EntityRepositoryJPA<User, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Optional<User> findByEmail(String email) {
        var result = em.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public Optional<User> findByUsername(String username) {
        var result = em.createQuery(
                        "SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList();

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public void flush() {
        em.flush();
    }

    public List<User> searchByUsername(String query) {
        return em.createQuery(
                        "SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(:query) ORDER BY u.username", User.class)
                .setParameter("query", "%" + query + "%")
                .setMaxResults(10) // Limit to 10 results
                .getResultList();
    }
}
