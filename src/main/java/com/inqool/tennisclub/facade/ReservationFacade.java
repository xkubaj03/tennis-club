package com.inqool.tennisclub.facade;

import com.inqool.tennisclub.api.CreateReservationDto;
import com.inqool.tennisclub.api.ReservationDto;
import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.model.CustomerEntity;
import com.inqool.tennisclub.data.model.ReservationEntity;
import com.inqool.tennisclub.mappers.CustomerMapper;
import com.inqool.tennisclub.mappers.ReservationMapper;
import com.inqool.tennisclub.service.CourtService;
import com.inqool.tennisclub.service.CustomerService;
import com.inqool.tennisclub.service.ReservationService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ReservationFacade {
    private final ReservationService reservationService;
    private final CustomerService customerService;
    private final CourtService courtService;
    private final ReservationMapper reservationMapper;
    private final CustomerMapper customerMapper;

    public ReservationFacade(
            ReservationService reservationService,
            CustomerService customerService,
            CourtService courtService,
            ReservationMapper reservationMapper,
            CustomerMapper customerMapper) {
        this.reservationService = reservationService;
        this.customerService = customerService;
        this.courtService = courtService;
        this.reservationMapper = reservationMapper;
        this.customerMapper = customerMapper;
    }

    @Transactional
    public ReservationDto create(CreateReservationDto createReservationDto) {
        ReservationEntity reservation = prepareReservationEntity(createReservationDto);
        ReservationEntity saved = reservationService.create(reservation);
        return reservationMapper.toDto(saved);
    }

    public Optional<ReservationDto> findById(Long id) {
        return reservationService.findById(id).map(reservationMapper::toDto);
    }

    public List<ReservationDto> findByCourt(Integer number) {
        return reservationMapper.toDtoList(reservationService.findAll()); // TODO fix
    }

    public List<ReservationDto> findByPhone(String phone, boolean futureOnly) {
        return reservationMapper.toDtoList(reservationService.findAll()); // TODO fix
    }

    public ReservationDto update(Long id, CreateReservationDto createReservationDto) {
        ReservationEntity reservation = prepareReservationEntity(createReservationDto);
        reservation.setId(id);
        ReservationEntity saved = reservationService.update(reservation);
        return reservationMapper.toDto(saved);
    }

    public List<ReservationDto> findAll() {
        return reservationService.findAll().stream() // TODO Change back after properly configurating mapping
                .map(reservation -> {
                    ReservationDto dto = reservationMapper.toDto(reservation);
                    BigDecimal totalCost = reservationService.calculateTotalPrice(reservation);
                    dto.setTotalPrice(totalCost);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        reservationService.deleteById(id);
    }

    private ReservationEntity prepareReservationEntity(CreateReservationDto createReservationDto) {
        CustomerEntity customer = customerMapper.toCustomerEntity(createReservationDto);
        CustomerEntity persistedCustomer = customerService.createIfNotExist(customer);

        ReservationEntity reservation = reservationMapper.toEntity(createReservationDto);
        reservation.setCustomer(persistedCustomer);

        CourtEntity court = courtService
                .findByCourtNumber(createReservationDto.getCourtNumber())
                .orElseThrow();

        reservation.setCourt(court);

        return reservation;
    }
}
