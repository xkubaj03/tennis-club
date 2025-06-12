package com.inqool.tennisclub.data.repository.impl;

import com.inqool.tennisclub.data.model.UserEntity;
import com.inqool.tennisclub.data.repository.UserRepository;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<UserEntity, Long> implements UserRepository {

    public UserRepositoryImpl() {
        super(UserEntity.class);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        String queryStr = "SELECT u FROM UserEntity u WHERE u.username = :username AND u.active = true";
        TypedQuery<UserEntity> query = entityManager.createQuery(queryStr, UserEntity.class);
        query.setParameter("username", username);

        List<UserEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
