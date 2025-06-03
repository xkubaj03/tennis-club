package com.inqool.tennisclub.data.repository;

import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Court entities
 */
public interface CourtRepository extends BaseRepository<CourtEntity, Long> {

    /**
     * Find court by court number
     */
    Optional<CourtEntity> findByCourtNumber(Integer courtNumber);

    /**
     * Find courts by surface type
     */
    List<CourtEntity> findByCourtSurface(CourtSurfaceEntity courtSurface);
}
