package org.example.mematch.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import org.example.mematch.infrastructure.persistence.EntityRepository;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

public abstract class EntityRepositoryJPA<T, ID> implements EntityRepository<T, ID> {

    protected abstract EntityManager getEntityManager();

    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public EntityRepositoryJPA() {
        this.entityClass =
                (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
    }

    @Override
    public T save(T entity) {
        return getEntityManager().merge(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(getEntityManager().find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        return getEntityManager()
                .createQuery("FROM " + entityClass.getSimpleName(), entityClass)
                .getResultList();
    }

    @Override
    public void delete(T entity) {
        getEntityManager().remove(
                getEntityManager().contains(entity)
                        ? entity
                        : getEntityManager().merge(entity)
        );
    }
}
