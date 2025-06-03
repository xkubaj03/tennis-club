package com.inqool.tennisclub.data.repository.impl;

import com.inqool.tennisclub.data.model.ReservationEntity;
import com.inqool.tennisclub.data.repository.ReservationRepository;
import jakarta.persistence.TypedQuery;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ReservationRepositoryImpl extends BaseRepositoryImpl<ReservationEntity, Long>
        implements ReservationRepository {

    public ReservationRepositoryImpl() {
        super(ReservationEntity.class);
    }

    /**
     * Validates court availability parameters
     * @param courtId Court ID (cannot be null)
     * @param startTime Start time (cannot be null)
     * @param endTime End time (cannot be null, must be after startTime)
     * @throws IllegalArgumentException if any validation fails
     */
    private void validateCourtAvailabilityParams(Long courtId, OffsetDateTime startTime, OffsetDateTime endTime) {
        if (courtId == null) {
            throw new IllegalArgumentException("Court ID cannot be null");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    private boolean isInvalidPhoneNumber(String phoneNumber) {
        return phoneNumber == null || phoneNumber.trim().isEmpty();
    }

    private String getTrimmedPhoneNumber(String phoneNumber) {
        return phoneNumber.trim();
    }

    @Override
    public List<ReservationEntity> findByCourtNumberOrderByCreatedAt(Integer courtNumber) {
        if (courtNumber == null) {
            return List.of();
        }

        String queryStr = "SELECT r FROM ReservationEntity r "
                + "WHERE r.court.courtNumber = :courtNumber AND r.active = true "
                + "ORDER BY r.createdAt";
        TypedQuery<ReservationEntity> query = entityManager.createQuery(queryStr, ReservationEntity.class);
        query.setParameter("courtNumber", courtNumber);
        return query.getResultList();
    }

    @Override
    public List<ReservationEntity> findByCustomerPhoneNumber(String phoneNumber) {
        if (isInvalidPhoneNumber(phoneNumber)) {
            return List.of();
        }

        String queryStr = "SELECT r FROM ReservationEntity r "
                + "WHERE r.customer.phoneNumber = :phoneNumber AND r.active = true";
        TypedQuery<ReservationEntity> query = entityManager.createQuery(queryStr, ReservationEntity.class);
        query.setParameter("phoneNumber", getTrimmedPhoneNumber(phoneNumber));
        return query.getResultList();
    }

    @Override
    public List<ReservationEntity> findFutureReservationsByCustomerPhoneNumber(String phoneNumber) {
        if (isInvalidPhoneNumber(phoneNumber)) {
            return List.of();
        }

        String queryStr = "SELECT r FROM ReservationEntity r "
                + "WHERE r.customer.phoneNumber = :phoneNumber "
                + "AND r.startTime > :now AND r.active = true";
        TypedQuery<ReservationEntity> query = entityManager.createQuery(queryStr, ReservationEntity.class);
        query.setParameter("phoneNumber", getTrimmedPhoneNumber(phoneNumber));
        query.setParameter("now", OffsetDateTime.now());
        return query.getResultList();
    }

    @Override
    public List<ReservationEntity> findOverlappingReservations(
            Long courtId, OffsetDateTime startTime, OffsetDateTime endTime) {
        return findOverlappingReservations(courtId, startTime, endTime, null);
    }

    @Override
    public List<ReservationEntity> findOverlappingReservations(
            Long courtId, OffsetDateTime startTime, OffsetDateTime endTime, Long excludeReservationId) {

        validateCourtAvailabilityParams(courtId, startTime, endTime);

        String queryStr = "SELECT r FROM ReservationEntity r WHERE r.court.id = :courtId "
                + "AND r.active = true "
                + "AND NOT (r.endTime <= :startTime OR r.startTime >= :endTime)";

        if (excludeReservationId != null) {
            queryStr += " AND r.id != :excludeReservationId";
        }

        TypedQuery<ReservationEntity> query = entityManager.createQuery(queryStr, ReservationEntity.class);
        query.setParameter("courtId", courtId);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);

        if (excludeReservationId != null) {
            query.setParameter("excludeReservationId", excludeReservationId);
        }

        return query.getResultList();
    }

    @Override
    public boolean isCourtAvailable(Long courtId, OffsetDateTime startTime, OffsetDateTime endTime) {
        return isCourtAvailable(courtId, startTime, endTime, null);
    }

    @Override
    public boolean isCourtAvailable(
            Long courtId, OffsetDateTime startTime, OffsetDateTime endTime, Long excludeReservationId) {

        validateCourtAvailabilityParams(courtId, startTime, endTime);

        String queryStr = "SELECT COUNT(r) FROM ReservationEntity r WHERE r.court.id = :courtId "
                + "AND r.active = true "
                + "AND NOT (r.endTime <= :startTime OR r.startTime >= :endTime)";

        if (excludeReservationId != null) {
            queryStr += " AND r.id != :excludeReservationId";
        }

        TypedQuery<Long> query = entityManager.createQuery(queryStr, Long.class);
        query.setParameter("courtId", courtId);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);

        if (excludeReservationId != null) {
            query.setParameter("excludeReservationId", excludeReservationId);
        }

        return query.getSingleResult() == 0;
    }
}
