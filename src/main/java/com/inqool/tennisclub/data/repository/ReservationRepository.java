package com.inqool.tennisclub.data.repository;

import com.inqool.tennisclub.data.model.ReservationEntity;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository interface for Reservation entities
 */
public interface ReservationRepository extends BaseRepository<ReservationEntity, Long> {

    /**
     * Find reservations by court number, ordered by creation date
     */
    List<ReservationEntity> findByCourtNumberOrderByCreatedAt(Integer courtNumber);

    /**
     * Find reservations by customer phone number
     */
    List<ReservationEntity> findByCustomerPhoneNumber(String phoneNumber);

    /**
     * Find future reservations by customer phone number
     */
    List<ReservationEntity> findFutureReservationsByCustomerPhoneNumber(String phoneNumber);

    /**
     * Check if court is available for given time period
     */
    boolean isCourtAvailable(Long courtId, OffsetDateTime startTime, OffsetDateTime endTime);
}
