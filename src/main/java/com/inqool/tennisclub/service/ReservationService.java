package com.inqool.tennisclub.service;

import com.inqool.tennisclub.data.model.ReservationEntity;
import com.inqool.tennisclub.data.repository.CustomerRepository;
import com.inqool.tennisclub.data.repository.ReservationRepository;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private final CustomerRepository customerRepository;

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(CustomerRepository customerRepository, ReservationRepository reservationRepository) {
        // TODO check for collision
        this.customerRepository = customerRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationEntity create(ReservationEntity entity) {
        return reservationRepository.save(entity);
    }

    public Optional<ReservationEntity> findById(Long id) {
        return Optional.ofNullable(reservationRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation with id " + id + " not found")));
    }

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
