package com.inqool.tennisclub.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inqool.tennisclub.api.CreateReservationDto;
import com.inqool.tennisclub.api.ReservationDto;
import com.inqool.tennisclub.data.model.enums.GameType;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import com.inqool.tennisclub.facade.ReservationFacade;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class ReservationControllerTest {
    @Mock
    private ReservationFacade reservationFacade;

    @InjectMocks
    private ReservationRestController reservationRestController;

    @Test
    void create_validReservationDto_returnsCreatedReservation() {
        OffsetDateTime startTime = OffsetDateTime.now().plusHours(1);
        OffsetDateTime endTime = startTime.plusHours(2);
        CreateReservationDto createDto = CreateReservationDto.builder()
                .phoneNumber("+420123456789")
                .customerName("Jan Novak")
                .gameType(GameType.SINGLES)
                .courtNumber(1)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        ReservationDto expectedDto = ReservationDto.builder()
                .id(1L)
                .courtId(1L)
                .phoneNumber("+420123456789")
                .customerName("Jan Novak")
                .courtNumber(1)
                .gameType(GameType.SINGLES)
                .startTime(startTime)
                .endTime(endTime)
                .totalPrice(BigDecimal.valueOf(120.0))
                .createdAt(OffsetDateTime.now())
                .build();
        when(reservationFacade.create(createDto)).thenReturn(expectedDto);

        ResponseEntity<ReservationDto> response = reservationRestController.create(createDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedDto);
        verify(reservationFacade, times(1)).create(createDto);
    }

    @Test
    void findAll_returnsListOfReservations() {
        List<ReservationDto> expectedList = List.of(ReservationDto.builder()
                .id(1L)
                .courtId(1L)
                .phoneNumber("+420123456789")
                .customerName("Jan Novak")
                .courtNumber(1)
                .gameType(GameType.SINGLES)
                .startTime(OffsetDateTime.now().plusHours(1))
                .endTime(OffsetDateTime.now().plusHours(3))
                .totalPrice(BigDecimal.valueOf(120.0))
                .createdAt(OffsetDateTime.now())
                .build());
        when(reservationFacade.findAll()).thenReturn(expectedList);

        ResponseEntity<List<ReservationDto>> response = reservationRestController.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedList);
        verify(reservationFacade, times(1)).findAll();
    }

    @Test
    void findById_existingId_returnsReservation() {
        Long id = 1L;
        ReservationDto expectedDto = ReservationDto.builder()
                .id(1L)
                .courtId(1L)
                .phoneNumber("+420123456789")
                .customerName("Jan Novak")
                .courtNumber(1)
                .gameType(GameType.SINGLES)
                .startTime(OffsetDateTime.now().plusHours(1))
                .endTime(OffsetDateTime.now().plusHours(3))
                .totalPrice(BigDecimal.valueOf(120.0))
                .createdAt(OffsetDateTime.now())
                .build();
        when(reservationFacade.findById(id)).thenReturn(expectedDto);

        ResponseEntity<ReservationDto> response = reservationRestController.findById(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedDto);
    }

    @Test
    void findById_nonExistingId_throwsException() {
        Long id = 99L;
        when(reservationFacade.findById(id))
                .thenThrow(new EntityNotFoundException("Reservation with id " + id + " not found"));

        assertThrows(EntityNotFoundException.class, () -> reservationRestController.findById(id));
    }

    @Test
    void findByCourt_existingCourtNumber_returnsReservations() {
        Integer courtNumber = 1;
        List<ReservationDto> expectedList = List.of(ReservationDto.builder()
                .id(1L)
                .courtId(1L)
                .phoneNumber("+420123456789")
                .customerName("Jan Novak")
                .courtNumber(1)
                .gameType(GameType.SINGLES)
                .startTime(OffsetDateTime.now().plusHours(1))
                .endTime(OffsetDateTime.now().plusHours(3))
                .totalPrice(BigDecimal.valueOf(120.0))
                .createdAt(OffsetDateTime.now())
                .build());
        when(reservationFacade.findByCourt(courtNumber)).thenReturn(expectedList);

        ResponseEntity<List<ReservationDto>> response = reservationRestController.findByCourt(courtNumber);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedList);
        verify(reservationFacade, times(1)).findByCourt(courtNumber);
    }

    @Test
    void findByPhone_existingPhoneNumber_returnsReservations() {
        String phoneNumber = "+420123456789";
        boolean futureOnly = false;
        List<ReservationDto> expectedList = List.of(ReservationDto.builder()
                .id(1L)
                .courtId(1L)
                .phoneNumber("+420123456789")
                .customerName("Jan Novak")
                .courtNumber(1)
                .gameType(GameType.SINGLES)
                .startTime(OffsetDateTime.now().plusHours(1))
                .endTime(OffsetDateTime.now().plusHours(3))
                .totalPrice(BigDecimal.valueOf(120.0))
                .createdAt(OffsetDateTime.now())
                .build());
        when(reservationFacade.findByPhone(phoneNumber, futureOnly)).thenReturn(expectedList);

        ResponseEntity<List<ReservationDto>> response = reservationRestController.findByPhone(phoneNumber, futureOnly);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedList);
        verify(reservationFacade, times(1)).findByPhone(phoneNumber, futureOnly);
    }

    @Test
    void findByPhone_withFutureOnlyTrue_returnsReservations() {
        String phoneNumber = "+420123456789";
        boolean futureOnly = true;
        List<ReservationDto> expectedList = List.of(ReservationDto.builder()
                .id(1L)
                .courtId(1L)
                .phoneNumber("+420123456789")
                .customerName("Jan Novak")
                .courtNumber(1)
                .gameType(GameType.SINGLES)
                .startTime(OffsetDateTime.now().plusHours(1))
                .endTime(OffsetDateTime.now().plusHours(3))
                .totalPrice(BigDecimal.valueOf(120.0))
                .createdAt(OffsetDateTime.now())
                .build());
        when(reservationFacade.findByPhone(phoneNumber, futureOnly)).thenReturn(expectedList);

        ResponseEntity<List<ReservationDto>> response = reservationRestController.findByPhone(phoneNumber, futureOnly);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedList);
        verify(reservationFacade, times(1)).findByPhone(phoneNumber, futureOnly);
    }

    @Test
    void update_existingId_updatesReservation() {
        Long id = 1L;
        OffsetDateTime startTime = OffsetDateTime.now().plusHours(2);
        OffsetDateTime endTime = startTime.plusHours(2);
        CreateReservationDto updateDto = CreateReservationDto.builder()
                .phoneNumber("+420987654321")
                .customerName("Pavel Svoboda")
                .gameType(GameType.DOUBLES)
                .courtNumber(2)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        ReservationDto updatedDto = ReservationDto.builder()
                .id(1L)
                .courtId(2L)
                .phoneNumber("+420987654321")
                .customerName("Pavel Svoboda")
                .courtNumber(2)
                .gameType(GameType.DOUBLES)
                .startTime(startTime)
                .endTime(endTime)
                .totalPrice(BigDecimal.valueOf(160.0))
                .createdAt(OffsetDateTime.now())
                .build();
        when(reservationFacade.update(id, updateDto)).thenReturn(updatedDto);

        ResponseEntity<ReservationDto> response = reservationRestController.update(id, updateDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedDto);
        verify(reservationFacade, times(1)).update(id, updateDto);
    }

    @Test
    void delete_existingId_deletesReservation() {
        Long id = 1L;
        doNothing().when(reservationFacade).deleteById(id);

        ResponseEntity<Void> response = reservationRestController.delete(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(reservationFacade, times(1)).deleteById(id);
    }
}
