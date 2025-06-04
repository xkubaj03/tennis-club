package com.inqool.tennisclub.service;

import com.inqool.tennisclub.data.model.ReservationEntity;
import com.inqool.tennisclub.data.repository.ReservationRepository;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import com.inqool.tennisclub.exceptions.ReservationAlreadyExist;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public ReservationEntity create(ReservationEntity entity) {
        if (!reservationRepository.isCourtAvailable(
                entity.getCourt().getId(), entity.getStartTime(), entity.getEndTime())) {
            throw new ReservationAlreadyExist("Reservation for this court at this time already exists");
        }

        return reservationRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public ReservationEntity findById(Long id) {
        return reservationRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<ReservationEntity> findByCourtNumber(Integer number) {
        return reservationRepository.findByCourtNumberOrderByCreatedAt(number);
    }

    @Transactional(readOnly = true)
    public List<ReservationEntity> findByPhoneNumber(String phone, boolean futureOnly) {
        if (futureOnly) {
            return reservationRepository.findFutureReservationsByCustomerPhoneNumber(phone);
        }

        return reservationRepository.findByCustomerPhoneNumber(phone);
    }

    @Transactional(readOnly = true)
    public List<ReservationEntity> findAll() {
        return reservationRepository.findAll();
    }

    public ReservationEntity update(ReservationEntity entity) {
        reservationRepository
                .findById(entity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Reservation with id " + entity.getId() + " not found"));

        return reservationRepository.save(entity);
    }

    public void deleteById(Long id) {
        reservationRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation with id " + id + " not found"));

        reservationRepository.deleteById(id);
    }

    public BigDecimal calculateTotalPrice(ReservationEntity reservation) {
        long minutes = Duration.between(reservation.getStartTime(), reservation.getEndTime())
                .toMinutes();

        BigDecimal costPerMinute = reservation.getCourt().getCourtSurface().getCostPerMinute();
        BigDecimal baseCost = costPerMinute.multiply(BigDecimal.valueOf(minutes));

        BigDecimal multiplier = reservation.getGameType().getPriceMultiplier();

        return baseCost.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}
