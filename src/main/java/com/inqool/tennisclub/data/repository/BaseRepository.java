package com.inqool.tennisclub.data.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic base repository interface providing common CRUD operations
 * with soft delete support.
 *
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface BaseRepository<T, ID> {

    /**
     * Save entity (create or update)
     */
    T save(T entity);

    /**
     * Find entity by ID (only active entities)
     */
    Optional<T> findById(ID id);

    /**
     * Find all active entities
     */
    List<T> findAll();

    /**
     * Soft delete entity by ID
     */
    void deleteById(ID id);

    /**
     * Soft delete entity
     */
    void delete(T entity);

    /**
     * Check if entity exists by ID (only active entities)
     */
    boolean existsById(ID id);

    /**
     * Count all active entities
     */
    long count();
}
