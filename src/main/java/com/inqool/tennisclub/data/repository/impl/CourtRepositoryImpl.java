package com.inqool.tennisclub.data.repository.impl;

import com.inqool.tennisclub.data.model.*;
import com.inqool.tennisclub.data.repository.*;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CourtRepositoryImpl extends BaseRepositoryImpl<CourtEntity, Long> implements CourtRepository {

    public CourtRepositoryImpl() {
        super(CourtEntity.class);
    }

    @Override
    public Optional<CourtEntity> findByCourtNumber(Integer courtNumber) {
        if (courtNumber == null) {
            return Optional.empty();
        }
        String queryStr = "SELECT c FROM CourtEntity c WHERE c.courtNumber = :courtNumber AND c.active = true";
        TypedQuery<CourtEntity> query = entityManager.createQuery(queryStr, CourtEntity.class);
        query.setParameter("courtNumber", courtNumber);

        List<CourtEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<CourtEntity> findByCourtSurface(CourtSurfaceEntity courtSurface) {
        if (courtSurface == null) {
            return List.of();
        }
        String queryStr = "SELECT c FROM CourtEntity c WHERE c.courtSurface = :courtSurface AND c.active = true";
        TypedQuery<CourtEntity> query = entityManager.createQuery(queryStr, CourtEntity.class);
        query.setParameter("courtSurface", courtSurface);
        return query.getResultList();
    }
}
