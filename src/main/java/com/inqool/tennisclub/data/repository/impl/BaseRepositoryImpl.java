package com.inqool.tennisclub.data.repository.impl;

import com.inqool.tennisclub.data.repository.BaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * Abstract base implementation of BaseRepository providing common CRUD operations
 * with soft delete support using JPA EntityManager.
 *
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public abstract class BaseRepositoryImpl<T, ID> implements BaseRepository<T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> entityClass;

    protected BaseRepositoryImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    @Transactional
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        if (getId(entity) == null) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) {
            return Optional.empty();
        }
        String queryStr = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.id = :id AND e.active = true";

        TypedQuery<T> query = entityManager.createQuery(queryStr, entityClass);
        query.setParameter("id", id);

        List<T> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<T> findAll() {
        String queryStr = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.active = true";
        TypedQuery<T> query = entityManager.createQuery(queryStr, entityClass);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        Optional<T> entity = findById(id);
        entity.ifPresent(this::delete);
    }

    @Override
    @Transactional
    public void delete(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        try {
            // Soft delete - set active field to false
            Field activeField = entityClass.getDeclaredField("active");
            activeField.setAccessible(true);
            activeField.set(entity, false);
            entityManager.merge(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Entity must have 'active' field for soft delete", e);
        }
    }

    @Override
    public boolean existsById(ID id) {
        if (id == null) {
            return false;
        }
        String queryStr =
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e WHERE e.id = :id AND e.active = true";
        TypedQuery<Long> query = entityManager.createQuery(queryStr, Long.class);
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }

    @Override
    public long count() {
        String queryStr = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e WHERE e.active = true";
        TypedQuery<Long> query = entityManager.createQuery(queryStr, Long.class);
        return query.getSingleResult();
    }

    /**
     * Extract ID from entity using reflection
     */
    @SuppressWarnings("unchecked")
    private ID getId(T entity) {
        try {
            Field idField = entityClass.getDeclaredField("id");
            idField.setAccessible(true);
            return (ID) idField.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Entity must have 'id' field", e);
        }
    }
}
