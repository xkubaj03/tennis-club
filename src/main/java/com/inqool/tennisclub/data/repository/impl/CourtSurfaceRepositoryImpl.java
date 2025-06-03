package com.inqool.tennisclub.data.repository.impl;

import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import com.inqool.tennisclub.data.repository.CourtSurfaceRepository;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CourtSurfaceRepositoryImpl extends BaseRepositoryImpl<CourtSurfaceEntity, Long>
        implements CourtSurfaceRepository {

    public CourtSurfaceRepositoryImpl() {
        super(CourtSurfaceEntity.class);
    }

    @Override
    public Optional<CourtSurfaceEntity> findBySurfaceName(String surfaceName) {
        if (surfaceName == null || surfaceName.trim().isEmpty()) {
            return Optional.empty();
        }
        String queryStr =
                "SELECT cs FROM CourtSurfaceEntity cs WHERE cs.surfaceName = :surfaceName AND cs.active = true";
        TypedQuery<CourtSurfaceEntity> query = entityManager.createQuery(queryStr, CourtSurfaceEntity.class);
        query.setParameter("surfaceName", surfaceName);

        List<CourtSurfaceEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
