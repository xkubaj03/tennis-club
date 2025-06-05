package com.inqool.tennisclub.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.inqool.tennisclub.data.model.*;
import com.inqool.tennisclub.data.model.enums.GameType;
import com.inqool.tennisclub.data.repository.ReservationRepository;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import com.inqool.tennisclub.exceptions.ReservationAlreadyExist;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private ReservationEntity testReservation;
    private ReservationEntity secondReservation;
    private CourtEntity testCourt;
    private CourtSurfaceEntity grassSurface;
    private CustomerEntity testCustomer;

    @BeforeEach
    void setUp() {
        CourtSurfaceEntity claySurface = new CourtSurfaceEntity();
        claySurface.setId(1L);
        claySurface.setSurfaceName("Clay");
        claySurface.setSurfaceDescription("Red clay surface");
        claySurface.setCostPerMinute(BigDecimal.valueOf(0.15));
        claySurface.setActive(true);

        grassSurface = new CourtSurfaceEntity();
        grassSurface.setId(2L);
        grassSurface.setSurfaceName("Grass");
        grassSurface.setSurfaceDescription("Natural grass surface");
        grassSurface.setCostPerMinute(BigDecimal.valueOf(0.20));
        grassSurface.setActive(true);

        testCourt = new CourtEntity();
        testCourt.setId(1L);
        testCourt.setCourtNumber(1);
        testCourt.setCourtSurface(claySurface);
        testCourt.setActive(true);

        testCustomer = new CustomerEntity();
        testCustomer.setId(1L);
        testCustomer.setPhoneNumber("+420123456789");
        testCustomer.setName("John Doe");
        testCustomer.setActive(true);

        testReservation = new ReservationEntity();
        testReservation.setId(1L);
        testReservation.setCreatedAt(OffsetDateTime.now());
        testReservation.setGameType(GameType.SINGLES);
        testReservation.setStartTime(OffsetDateTime.now().plusHours(1));
        testReservation.setEndTime(OffsetDateTime.now().plusHours(2));
        testReservation.setCustomer(testCustomer);
        testReservation.setCourt(testCourt);
        testReservation.setActive(true);

        secondReservation = new ReservationEntity();
        secondReservation.setId(2L);
        secondReservation.setCreatedAt(OffsetDateTime.now());
        secondReservation.setGameType(GameType.DOUBLES);
        secondReservation.setStartTime(OffsetDateTime.now().plusHours(3));
        secondReservation.setEndTime(OffsetDateTime.now().plusHours(4));
        secondReservation.setCustomer(testCustomer);
        secondReservation.setCourt(testCourt);
        secondReservation.setActive(true);
    }

    @Test
    void create_validReservationCourtAvailable_returnsCreatedReservation() {
        ReservationEntity newReservation = new ReservationEntity();
        newReservation.setGameType(GameType.SINGLES);
        newReservation.setStartTime(OffsetDateTime.now().plusHours(5));
        newReservation.setEndTime(OffsetDateTime.now().plusHours(6));
        newReservation.setCustomer(testCustomer);
        newReservation.setCourt(testCourt);
        newReservation.setActive(true);

        ReservationEntity savedReservation = new ReservationEntity();
        savedReservation.setId(3L);
        savedReservation.setGameType(GameType.SINGLES);
        savedReservation.setStartTime(OffsetDateTime.now().plusHours(5));
        savedReservation.setEndTime(OffsetDateTime.now().plusHours(6));
        savedReservation.setCustomer(testCustomer);
        savedReservation.setCourt(testCourt);
        savedReservation.setActive(true);

        when(reservationRepository.isCourtAvailable(
                        testCourt.getId(), newReservation.getStartTime(), newReservation.getEndTime()))
                .thenReturn(true);
        when(reservationRepository.save(newReservation)).thenReturn(savedReservation);

        ReservationEntity result = reservationService.create(newReservation);

        assertThat(result).isEqualTo(savedReservation);
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getGameType()).isEqualTo(GameType.SINGLES);
        verify(reservationRepository, times(1))
                .isCourtAvailable(testCourt.getId(), newReservation.getStartTime(), newReservation.getEndTime());
        verify(reservationRepository, times(1)).save(newReservation);
    }

    @Test
    void create_courtNotAvailable_throwsReservationAlreadyExist() {
        ReservationEntity newReservation = new ReservationEntity();
        newReservation.setGameType(GameType.SINGLES);
        newReservation.setStartTime(OffsetDateTime.now().plusHours(1));
        newReservation.setEndTime(OffsetDateTime.now().plusHours(2));
        newReservation.setCustomer(testCustomer);
        newReservation.setCourt(testCourt);

        when(reservationRepository.isCourtAvailable(
                        testCourt.getId(), newReservation.getStartTime(), newReservation.getEndTime()))
                .thenReturn(false);

        assertThatThrownBy(() -> reservationService.create(newReservation))
                .isInstanceOf(ReservationAlreadyExist.class)
                .hasMessage("Reservation for this court at this time already exists");

        verify(reservationRepository, times(1))
                .isCourtAvailable(testCourt.getId(), newReservation.getStartTime(), newReservation.getEndTime());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void findById_existingId_returnsReservation() {
        Long id = 1L;
        when(reservationRepository.findById(id)).thenReturn(Optional.of(testReservation));

        ReservationEntity result = reservationService.findById(id);

        assertThat(result).isEqualTo(testReservation);
        assertThat(result.getGameType()).isEqualTo(GameType.SINGLES);
        verify(reservationRepository, times(1)).findById(id);
    }

    @Test
    void findById_nonExistingId_throwsEntityNotFoundException() {
        Long id = 99L;
        when(reservationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Reservation with id " + id + " not found");

        verify(reservationRepository, times(1)).findById(id);
    }

    @Test
    void findByCourtNumber_existingNumber_returnsReservationList() {
        Integer courtNumber = 1;
        List<ReservationEntity> reservations = List.of(testReservation, secondReservation);
        when(reservationRepository.findByCourtNumberOrderByCreatedAt(courtNumber))
                .thenReturn(reservations);

        List<ReservationEntity> result = reservationService.findByCourtNumber(courtNumber);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testReservation, secondReservation);
        verify(reservationRepository, times(1)).findByCourtNumberOrderByCreatedAt(courtNumber);
    }

    @Test
    void findByCourtNumber_nonExistingNumber_returnsEmptyList() {
        Integer courtNumber = 99;
        List<ReservationEntity> reservations = List.of();
        when(reservationRepository.findByCourtNumberOrderByCreatedAt(courtNumber))
                .thenReturn(reservations);

        List<ReservationEntity> result = reservationService.findByCourtNumber(courtNumber);

        assertThat(result).isEmpty();
        verify(reservationRepository, times(1)).findByCourtNumberOrderByCreatedAt(courtNumber);
    }

    @Test
    void findByPhoneNumber_futureOnlyTrue_returnsFutureReservations() {
        String phoneNumber = "+420123456789";
        boolean futureOnly = true;
        List<ReservationEntity> futureReservations = List.of(testReservation);
        when(reservationRepository.findFutureReservationsByCustomerPhoneNumber(phoneNumber))
                .thenReturn(futureReservations);

        List<ReservationEntity> result = reservationService.findByPhoneNumber(phoneNumber, futureOnly);

        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(testReservation);
        verify(reservationRepository, times(1)).findFutureReservationsByCustomerPhoneNumber(phoneNumber);
        verify(reservationRepository, never()).findByCustomerPhoneNumber(any());
    }

    @Test
    void findByPhoneNumber_futureOnlyFalse_returnsAllReservations() {
        String phoneNumber = "+420123456789";
        boolean futureOnly = false;
        List<ReservationEntity> allReservations = List.of(testReservation, secondReservation);
        when(reservationRepository.findByCustomerPhoneNumber(phoneNumber)).thenReturn(allReservations);

        List<ReservationEntity> result = reservationService.findByPhoneNumber(phoneNumber, futureOnly);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testReservation, secondReservation);
        verify(reservationRepository, times(1)).findByCustomerPhoneNumber(phoneNumber);
        verify(reservationRepository, never()).findFutureReservationsByCustomerPhoneNumber(any());
    }

    @Test
    void findAll_returnsListOfReservations() {
        List<ReservationEntity> reservations = List.of(testReservation, secondReservation);
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<ReservationEntity> result = reservationService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testReservation, secondReservation);
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void findAll_emptyRepository_returnsEmptyList() {
        List<ReservationEntity> reservations = List.of();
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<ReservationEntity> result = reservationService.findAll();

        assertThat(result).isEmpty();
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void update_existingReservation_returnsUpdatedReservation() {
        ReservationEntity reservationToUpdate = new ReservationEntity();
        reservationToUpdate.setId(1L);
        reservationToUpdate.setGameType(GameType.DOUBLES);
        reservationToUpdate.setStartTime(OffsetDateTime.now().plusHours(2));
        reservationToUpdate.setEndTime(OffsetDateTime.now().plusHours(3));
        reservationToUpdate.setCustomer(testCustomer);
        reservationToUpdate.setCourt(testCourt);
        reservationToUpdate.setActive(false);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(reservationToUpdate)).thenReturn(reservationToUpdate);

        ReservationEntity result = reservationService.update(reservationToUpdate);

        assertThat(result).isEqualTo(reservationToUpdate);
        assertThat(result.getGameType()).isEqualTo(GameType.DOUBLES);
        assertThat(result.isActive()).isFalse();
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(reservationToUpdate);
    }

    @Test
    void update_nonExistingReservation_throwsEntityNotFoundException() {
        ReservationEntity reservationToUpdate = new ReservationEntity();
        reservationToUpdate.setId(99L);
        reservationToUpdate.setGameType(GameType.SINGLES);

        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.update(reservationToUpdate))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Reservation with id " + 99L + " not found");

        verify(reservationRepository, times(1)).findById(99L);
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void deleteById_existingId_deletesReservation() {
        Long id = 1L;
        when(reservationRepository.findById(id)).thenReturn(Optional.of(testReservation));
        doNothing().when(reservationRepository).deleteById(id);

        reservationService.deleteById(id);

        verify(reservationRepository, times(1)).findById(id);
        verify(reservationRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteById_nonExistingId_throwsEntityNotFoundException() {
        Long id = 99L;
        when(reservationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.deleteById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Reservation with id " + id + " not found");

        verify(reservationRepository, times(1)).findById(id);
        verify(reservationRepository, never()).deleteById(any());
    }

    @Test
    void calculateTotalPrice_singlesGameClaySurface_returnsCorrectPrice() {
        ReservationEntity reservation = new ReservationEntity();
        reservation.setStartTime(OffsetDateTime.now());
        reservation.setEndTime(OffsetDateTime.now().plusMinutes(60));
        reservation.setGameType(GameType.SINGLES);
        reservation.setCourt(testCourt);

        BigDecimal result = reservationService.calculateTotalPrice(reservation);

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(9.00));
    }

    @Test
    void calculateTotalPrice_doublesGameClaySurface_returnsCorrectPrice() {
        ReservationEntity reservation = new ReservationEntity();
        reservation.setStartTime(OffsetDateTime.now());
        reservation.setEndTime(OffsetDateTime.now().plusMinutes(60));
        reservation.setGameType(GameType.DOUBLES);
        reservation.setCourt(testCourt);

        BigDecimal result = reservationService.calculateTotalPrice(reservation);

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(13.50));
    }

    @Test
    void calculateTotalPrice_singlesGameGrassSurface_returnsCorrectPrice() {
        CourtEntity grassCourt = new CourtEntity();
        grassCourt.setId(2L);
        grassCourt.setCourtNumber(2);
        grassCourt.setCourtSurface(grassSurface);
        grassCourt.setActive(true);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setStartTime(OffsetDateTime.now());
        reservation.setEndTime(OffsetDateTime.now().plusMinutes(90));
        reservation.setGameType(GameType.SINGLES);
        reservation.setCourt(grassCourt);

        BigDecimal result = reservationService.calculateTotalPrice(reservation);

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(18.00));
    }

    @Test
    void calculateTotalPrice_doublesGameGrassSurface_returnsCorrectPrice() {
        CourtEntity grassCourt = new CourtEntity();
        grassCourt.setId(2L);
        grassCourt.setCourtNumber(2);
        grassCourt.setCourtSurface(grassSurface);
        grassCourt.setActive(true);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setStartTime(OffsetDateTime.now());
        reservation.setEndTime(OffsetDateTime.now().plusMinutes(90));
        reservation.setGameType(GameType.DOUBLES);
        reservation.setCourt(grassCourt);

        BigDecimal result = reservationService.calculateTotalPrice(reservation);

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(27.00));
    }
}
