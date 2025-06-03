package com.inqool.tennisclub.data.repository;

import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import java.util.Optional;

/**
 * Repository interface for CourtSurface entities
 */
public interface CourtSurfaceRepository extends BaseRepository<CourtSurfaceEntity, Long> {

    /**
     * Find surface by name
     */
    Optional<CourtSurfaceEntity> findBySurfaceName(String surfaceName);
}
