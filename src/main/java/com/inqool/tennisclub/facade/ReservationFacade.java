package com.inqool.tennisclub.facade;

import com.inqool.tennisclub.api.CreateReservationDto;
import com.inqool.tennisclub.api.ReservationDto;
import com.inqool.tennisclub.data.model.ReservationEntity;
import com.inqool.tennisclub.mappers.ReservationMapper;
import com.inqool.tennisclub.service.CourtService;
import com.inqool.tennisclub.service.CustomerService;
import com.inqool.tennisclub.service.ReservationService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ReservationFacade {
    private final ReservationService reservationService;
    private final CustomerService customerService;
    private final CourtService courtService;
    private final ReservationMapper reservationMapper;

    public ReservationFacade(
            ReservationService reservationService,
            CustomerService customerService,
            CourtService courtService,
            ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.customerService = customerService;
        this.courtService = courtService;
        this.reservationMapper = reservationMapper;
    }

    @Transactional
    public ReservationDto create(CreateReservationDto createReservationDto) {
        ReservationEntity reservation =
                reservationMapper.toReservationEntity(createReservationDto, customerService, courtService);
        ReservationEntity saved = reservationService.create(reservation);
        return reservationMapper.toDto(saved, reservationService);
    }

    public Optional<ReservationDto> findById(Long id) {
        return reservationService.findById(id).map(entity -> reservationMapper.toDto(entity, reservationService));
    }

    public List<ReservationDto> findByCourt(Integer number) {
        return reservationMapper.toDtoList(reservationService.findByCourtNumber(number), reservationService);
    }

    public List<ReservationDto> findByPhone(String phone, boolean futureOnly) {
        return reservationMapper.toDtoList(reservationService.findByPhoneNumber(phone, futureOnly), reservationService);
    }

    public ReservationDto update(Long id, CreateReservationDto createReservationDto) {
        ReservationEntity reservation =
                reservationMapper.toReservationEntity(createReservationDto, customerService, courtService);
        reservation.setId(id);
        ReservationEntity saved = reservationService.update(reservation);
        return reservationMapper.toDto(saved, reservationService);
    }

    public List<ReservationDto> findAll() {
        return reservationMapper.toDtoList(reservationService.findAll(), reservationService);
    }

    public void deleteById(Long id) {
        reservationService.deleteById(id);
    }
}
