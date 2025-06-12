package com.inqool.tennisclub.data.repository;

import com.inqool.tennisclub.data.model.UserEntity;
import java.util.Optional;

/**
 * Repository interface for User entities
 */
public interface UserRepository extends BaseRepository<UserEntity, Long> {

    /**
     * Find user by username
     */
    Optional<UserEntity> findByUsername(String username);
}
