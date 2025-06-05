package com.inqool.tennisclub.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.inqool.tennisclub.api.CreateReservationDto;
import com.inqool.tennisclub.api.ReservationDto;
import com.inqool.tennisclub.data.model.*;
import com.inqool.tennisclub.data.model.enums.GameType;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import com.inqool.tennisclub.mappers.ReservationMapper;
import com.inqool.tennisclub.service.CourtService;
import com.inqool.tennisclub.service.CustomerService;
import com.inqool.tennisclub.service.ReservationService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReservationFacadeTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private CustomerService customerService;

    @Mock
    private CourtService courtService;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationFacade reservationFacade;

    private ReservationEntity testEntity;
    private ReservationDto testDto;
    private CreateReservationDto createDto;

    @BeforeEach
    void setUp() {
        OffsetDateTime startTime = OffsetDateTime.now().plusDays(1);
        OffsetDateTime endTime = startTime.plusHours(2);
        OffsetDateTime createdAt = OffsetDateTime.now();

        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        customer.setPhoneNumber("+420123456789");
        customer.setFirstName("Jan");
        customer.setLastName("Novák");

        CourtSurfaceEntity surface = new CourtSurfaceEntity();
        surface.setId(1L);
        surface.setSurfaceName("Clay");

        CourtEntity court = new CourtEntity();
        court.setId(1L);
        court.setCourtNumber(1);
        court.setCourtSurface(surface);

        testEntity = new ReservationEntity();
        testEntity.setId(1L);
        testEntity.setGameType(GameType.SINGLES);
        testEntity.setStartTime(startTime);
        testEntity.setEndTime(endTime);
        testEntity.setCreatedAt(createdAt);
        testEntity.setCustomer(customer);
        testEntity.setCourt(court);

        testDto = ReservationDto.builder()
                .id(1L)
                .courtId(1L)
                .phoneNumber("+420123456789")
                .customerName("Jan Novák")
                .courtNumber(1)
                .gameType(GameType.SINGLES)
                .startTime(startTime)
                .endTime(endTime)
                .totalPrice(BigDecimal.valueOf(150.00))
                .createdAt(createdAt)
                .build();

        createDto = CreateReservationDto.builder()
                .phoneNumber("+420123456789")
                .customerName("Jan Novák")
                .gameType(GameType.SINGLES)
                .courtNumber(1)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    @Test
    void create_validReservationDto_returnsCreatedReservation() {
        when(reservationMapper.toReservationEntity(createDto, customerService, courtService))
                .thenReturn(testEntity);
        when(reservationService.create(testEntity)).thenReturn(testEntity);
        when(reservationMapper.toDto(testEntity, reservationService)).thenReturn(testDto);

        ReservationDto result = reservationFacade.create(createDto);

        assertThat(result).isEqualTo(testDto);
        verify(reservationMapper, times(1)).toReservationEntity(createDto, customerService, courtService);
        verify(reservationService, times(1)).create(testEntity);
        verify(reservationMapper, times(1)).toDto(testEntity, reservationService);
    }

    @Test
    void findById_existingId_returnsReservation() {
        Long id = 1L;
        when(reservationService.findById(id)).thenReturn(testEntity);
        when(reservationMapper.toDto(testEntity, reservationService)).thenReturn(testDto);

        ReservationDto result = reservationFacade.findById(id);

        assertThat(result).isEqualTo(testDto);
        verify(reservationService, times(1)).findById(id);
        verify(reservationMapper, times(1)).toDto(testEntity, reservationService);
    }

    @Test
    void findById_nonExistingId_throwsException() {
        Long id = 99L;
        when(reservationService.findById(id)).thenThrow(new EntityNotFoundException("Reservation not found"));

        assertThrows(EntityNotFoundException.class, () -> reservationFacade.findById(id));

        verify(reservationService, times(1)).findById(id);
        verify(reservationMapper, never()).toDto(any(), any());
    }

    @Test
    void findByCourt_existingCourtNumber_returnsReservationList() {
        Integer courtNumber = 1;
        List<ReservationEntity> entities = List.of(testEntity);
        List<ReservationDto> dtos = List.of(testDto);

        when(reservationService.findByCourtNumber(courtNumber)).thenReturn(entities);
        when(reservationMapper.toDtoList(entities, reservationService)).thenReturn(dtos);

        List<ReservationDto> result = reservationFacade.findByCourt(courtNumber);

        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(dtos);
        verify(reservationService, times(1)).findByCourtNumber(courtNumber);
        verify(reservationMapper, times(1)).toDtoList(entities, reservationService);
    }

    @Test
    void findByCourt_nonExistingCourtNumber_returnsEmptyList() {
        Integer courtNumber = 99;
        List<ReservationEntity> entities = List.of();
        List<ReservationDto> dtos = List.of();

        when(reservationService.findByCourtNumber(courtNumber)).thenReturn(entities);
        when(reservationMapper.toDtoList(entities, reservationService)).thenReturn(dtos);

        List<ReservationDto> result = reservationFacade.findByCourt(courtNumber);

        assertThat(result).isEmpty();
        verify(reservationService, times(1)).findByCourtNumber(courtNumber);
        verify(reservationMapper, times(1)).toDtoList(entities, reservationService);
    }

    @Test
    void findByPhone_existingPhoneWithFutureOnly_returnsReservationList() {
        String phone = "+420123456789";
        boolean futureOnly = true;
        List<ReservationEntity> entities = List.of(testEntity);
        List<ReservationDto> dtos = List.of(testDto);

        when(reservationService.findByPhoneNumber(phone, futureOnly)).thenReturn(entities);
        when(reservationMapper.toDtoList(entities, reservationService)).thenReturn(dtos);

        List<ReservationDto> result = reservationFacade.findByPhone(phone, futureOnly);

        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(dtos);
        verify(reservationService, times(1)).findByPhoneNumber(phone, futureOnly);
        verify(reservationMapper, times(1)).toDtoList(entities, reservationService);
    }

    @Test
    void findByPhone_existingPhoneWithAllReservations_returnsReservationList() {
        String phone = "+420123456789";
        boolean futureOnly = false;
        List<ReservationEntity> entities = List.of(testEntity);
        List<ReservationDto> dtos = List.of(testDto);

        when(reservationService.findByPhoneNumber(phone, futureOnly)).thenReturn(entities);
        when(reservationMapper.toDtoList(entities, reservationService)).thenReturn(dtos);

        List<ReservationDto> result = reservationFacade.findByPhone(phone, futureOnly);

        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(dtos);
        verify(reservationService, times(1)).findByPhoneNumber(phone, futureOnly);
        verify(reservationMapper, times(1)).toDtoList(entities, reservationService);
    }

    @Test
    void findByPhone_nonExistingPhone_returnsEmptyList() {
        String phone = "+420999666999";
        boolean futureOnly = true;
        List<ReservationEntity> entities = List.of();
        List<ReservationDto> dtos = List.of();

        when(reservationService.findByPhoneNumber(phone, futureOnly)).thenReturn(entities);
        when(reservationMapper.toDtoList(entities, reservationService)).thenReturn(dtos);

        List<ReservationDto> result = reservationFacade.findByPhone(phone, futureOnly);

        assertThat(result).isEmpty();
        verify(reservationService, times(1)).findByPhoneNumber(phone, futureOnly);
        verify(reservationMapper, times(1)).toDtoList(entities, reservationService);
    }

    @Test
    void update_existingReservation_updatesReservation() {
        Long id = 1L;
        OffsetDateTime newStartTime = OffsetDateTime.now().plusDays(2);
        OffsetDateTime newEndTime = newStartTime.plusHours(3);

        CreateReservationDto updateDto = CreateReservationDto.builder()
                .phoneNumber("+420987654321")
                .customerName("Petr Svoboda")
                .gameType(GameType.DOUBLES)
                .courtNumber(2)
                .startTime(newStartTime)
                .endTime(newEndTime)
                .build();

        CustomerEntity updatedCustomer = new CustomerEntity();
        updatedCustomer.setId(2L);
        updatedCustomer.setPhoneNumber("+420987654321");
        updatedCustomer.setFirstName("Petr");
        updatedCustomer.setLastName("Svoboda");

        CourtEntity updatedCourt = new CourtEntity();
        updatedCourt.setId(2L);
        updatedCourt.setCourtNumber(2);

        ReservationEntity mappedEntity = new ReservationEntity();
        mappedEntity.setGameType(GameType.DOUBLES);
        mappedEntity.setStartTime(newStartTime);
        mappedEntity.setEndTime(newEndTime);
        mappedEntity.setCustomer(updatedCustomer);
        mappedEntity.setCourt(updatedCourt);

        ReservationEntity updatedEntity = new ReservationEntity();
        updatedEntity.setId(id);
        updatedEntity.setGameType(GameType.DOUBLES);
        updatedEntity.setStartTime(newStartTime);
        updatedEntity.setEndTime(newEndTime);
        updatedEntity.setCustomer(updatedCustomer);
        updatedEntity.setCourt(updatedCourt);

        ReservationDto updatedDto = ReservationDto.builder()
                .id(id)
                .phoneNumber("+420987654321")
                .customerName("Petr Svoboda")
                .gameType(GameType.DOUBLES)
                .courtNumber(2)
                .startTime(newStartTime)
                .endTime(newEndTime)
                .totalPrice(BigDecimal.valueOf(200.00))
                .build();

        when(reservationMapper.toReservationEntity(updateDto, customerService, courtService))
                .thenReturn(mappedEntity);
        when(reservationService.update(any(ReservationEntity.class))).thenReturn(updatedEntity);
        when(reservationMapper.toDto(updatedEntity, reservationService)).thenReturn(updatedDto);

        ReservationDto result = reservationFacade.update(id, updateDto);

        assertThat(result).isEqualTo(updatedDto);
        verify(reservationMapper, times(1)).toReservationEntity(updateDto, customerService, courtService);
        verify(reservationService, times(1))
                .update(argThat(entity -> entity.getId().equals(id)
                        && entity.getGameType().equals(GameType.DOUBLES)
                        && entity.getCustomer().getPhoneNumber().equals("+420987654321")
                        && entity.getCourt().getCourtNumber().equals(2)));
        verify(reservationMapper, times(1)).toDto(updatedEntity, reservationService);
    }

    @Test
    void findAll_returnsListOfReservations() {
        OffsetDateTime startTime2 = OffsetDateTime.now().plusDays(3);
        OffsetDateTime endTime2 = startTime2.plusHours(1);

        CustomerEntity secondCustomer = new CustomerEntity();
        secondCustomer.setId(2L);
        secondCustomer.setPhoneNumber("+420987654321");
        secondCustomer.setFirstName("Petr");
        secondCustomer.setLastName("Svoboda");

        CourtEntity secondCourt = new CourtEntity();
        secondCourt.setId(2L);
        secondCourt.setCourtNumber(2);

        ReservationEntity secondEntity = new ReservationEntity();
        secondEntity.setId(2L);
        secondEntity.setGameType(GameType.DOUBLES);
        secondEntity.setStartTime(startTime2);
        secondEntity.setEndTime(endTime2);
        secondEntity.setCustomer(secondCustomer);
        secondEntity.setCourt(secondCourt);

        ReservationDto secondDto = ReservationDto.builder()
                .id(2L)
                .phoneNumber("+420987654321")
                .customerName("Petr Svoboda")
                .gameType(GameType.DOUBLES)
                .courtNumber(2)
                .startTime(startTime2)
                .endTime(endTime2)
                .totalPrice(BigDecimal.valueOf(120.00))
                .build();

        List<ReservationEntity> entities = List.of(testEntity, secondEntity);
        List<ReservationDto> dtos = List.of(testDto, secondDto);

        when(reservationService.findAll()).thenReturn(entities);
        when(reservationMapper.toDtoList(entities, reservationService)).thenReturn(dtos);

        List<ReservationDto> result = reservationFacade.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(dtos);
        verify(reservationService, times(1)).findAll();
        verify(reservationMapper, times(1)).toDtoList(entities, reservationService);
    }

    @Test
    void findAll_emptyList_returnsEmptyList() {
        List<ReservationEntity> entities = List.of();
        List<ReservationDto> dtos = List.of();

        when(reservationService.findAll()).thenReturn(entities);
        when(reservationMapper.toDtoList(entities, reservationService)).thenReturn(dtos);

        List<ReservationDto> result = reservationFacade.findAll();

        assertThat(result).isEmpty();
        verify(reservationService, times(1)).findAll();
        verify(reservationMapper, times(1)).toDtoList(entities, reservationService);
    }

    @Test
    void deleteById_existingReservation_deletesReservation() {
        Long id = 1L;
        doNothing().when(reservationService).deleteById(id);

        reservationFacade.deleteById(id);

        verify(reservationService, times(1)).deleteById(id);
    }

    @Test
    void deleteById_nonExistingReservation_callsServiceDelete() {
        Long id = 99L;
        doNothing().when(reservationService).deleteById(id);

        reservationFacade.deleteById(id);

        verify(reservationService, times(1)).deleteById(id);
    }
}
