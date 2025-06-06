package com.inqool.tennisclub.mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.inqool.tennisclub.api.CreateReservationDto;
import com.inqool.tennisclub.api.ReservationDto;
import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import com.inqool.tennisclub.data.model.CustomerEntity;
import com.inqool.tennisclub.data.model.ReservationEntity;
import com.inqool.tennisclub.data.model.enums.GameType;
import com.inqool.tennisclub.service.CourtService;
import com.inqool.tennisclub.service.CustomerService;
import com.inqool.tennisclub.service.ReservationService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReservationMapperTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private CustomerService customerService;

    @Mock
    private CourtService courtService;

    private final ReservationMapper mapper = Mappers.getMapper(ReservationMapper.class);

    private CourtEntity testCourtEntity;

    private ReservationEntity testReservationEntity;

    private CustomerEntity testCustomerEntity;

    @BeforeEach
    void setUp() {
        CourtSurfaceEntity testCourtSurfaceEntity = new CourtSurfaceEntity();
        testCourtSurfaceEntity.setId(1L);
        testCourtSurfaceEntity.setSurfaceName("Clay");
        testCourtSurfaceEntity.setSurfaceDescription("Red clay surface");
        testCourtSurfaceEntity.setCostPerMinute(BigDecimal.valueOf(0.1));

        testCourtEntity = new CourtEntity();
        testCourtEntity.setId(1L);
        testCourtEntity.setCourtNumber(69);
        testCourtEntity.setCourtSurface(testCourtSurfaceEntity);

        testCustomerEntity = new CustomerEntity();
        testCustomerEntity.setId(1L);
        testCustomerEntity.setPhoneNumber("722666999");
        testCustomerEntity.setName("John Doe");

        testReservationEntity = new ReservationEntity();
        testReservationEntity.setId(1L);
        testReservationEntity.setStartTime(OffsetDateTime.now().plusHours(6));
        testReservationEntity.setEndTime(OffsetDateTime.now().plusHours(8));
        testReservationEntity.setGameType(GameType.SINGLES);
        testReservationEntity.setCourt(testCourtEntity);
        testReservationEntity.setCustomer(testCustomerEntity);
    }

    @Test
    void toDto_validEntity_returnsMappedDto() {
        ReservationEntity entity = new ReservationEntity();
        entity.setId(2L);
        entity.setStartTime(OffsetDateTime.now().plusHours(1));
        entity.setEndTime(OffsetDateTime.now().plusHours(2));
        entity.setGameType(GameType.SINGLES);
        entity.setCourt(testCourtEntity);
        entity.setCustomer(testCustomerEntity);
        entity.setCreatedAt(OffsetDateTime.now());
        when(reservationService.calculateTotalPrice(entity)).thenReturn(BigDecimal.valueOf(6));

        ReservationDto dto = mapper.toDto(entity, reservationService);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getStartTime()).isEqualTo(entity.getStartTime());
        assertThat(dto.getEndTime()).isEqualTo(entity.getEndTime());
        assertThat(dto.getGameType()).isEqualTo(GameType.SINGLES);
        assertThat(dto.getCourtId()).isEqualTo(1L);
        assertThat(dto.getCourtNumber()).isEqualTo(69);
        assertThat(dto.getCustomerName()).isEqualTo("John Doe");
        assertThat(dto.getPhoneNumber()).isEqualTo("722666999");
        assertThat(dto.getTotalPrice()).isEqualTo(BigDecimal.valueOf(6));
        assertThat(dto.getCreatedAt()).isEqualTo(entity.getCreatedAt());
    }

    @Test
    void toDtoList_validEntities_returnsMappedDtoList() {
        ReservationEntity entity = new ReservationEntity();
        entity.setId(2L);
        entity.setStartTime(OffsetDateTime.now().plusHours(1));
        entity.setEndTime(OffsetDateTime.now().plusHours(2));
        entity.setGameType(GameType.SINGLES);
        entity.setCourt(testCourtEntity);
        entity.setCustomer(testCustomerEntity);
        entity.setCreatedAt(OffsetDateTime.now());
        when(reservationService.calculateTotalPrice(entity)).thenReturn(BigDecimal.valueOf(6));
        when(reservationService.calculateTotalPrice(testReservationEntity)).thenReturn(BigDecimal.valueOf(12));

        List<ReservationDto> dtos = mapper.toDtoList(List.of(entity, testReservationEntity), reservationService);

        assertThat(dtos.get(0).getId()).isEqualTo(2L);
        assertThat(dtos.get(0).getCustomerName()).isEqualTo("John Doe");
        assertThat(dtos.get(0).getPhoneNumber()).isEqualTo("722666999");
        assertThat(dtos.get(0).getStartTime()).isEqualTo(entity.getStartTime());
        assertThat(dtos.get(0).getEndTime()).isEqualTo(entity.getEndTime());
        assertThat(dtos.get(0).getGameType()).isEqualTo(GameType.SINGLES);
        assertThat(dtos.get(0).getCourtId()).isEqualTo(1L);
        assertThat(dtos.get(0).getCourtNumber()).isEqualTo(69);
        assertThat(dtos.get(0).getTotalPrice()).isEqualTo(BigDecimal.valueOf(6));
        assertThat(dtos.get(0).getCreatedAt()).isEqualTo(entity.getCreatedAt());

        assertThat(dtos.get(1).getId()).isEqualTo(1L);
        assertThat(dtos.get(1).getCustomerName()).isEqualTo("John Doe");
        assertThat(dtos.get(1).getPhoneNumber()).isEqualTo("722666999");
        assertThat(dtos.get(1).getStartTime()).isEqualTo(testReservationEntity.getStartTime());
        assertThat(dtos.get(1).getEndTime()).isEqualTo(testReservationEntity.getEndTime());
        assertThat(dtos.get(1).getGameType()).isEqualTo(GameType.SINGLES);
        assertThat(dtos.get(1).getCourtId()).isEqualTo(1L);
        assertThat(dtos.get(1).getCourtNumber()).isEqualTo(69);
        assertThat(dtos.get(1).getTotalPrice()).isEqualTo(BigDecimal.valueOf(12));
        assertThat(dtos.get(1).getCreatedAt()).isEqualTo(testReservationEntity.getCreatedAt());
    }

    @Test
    void toEntity_validCreateDto_returnsMappedEntity() {
        CreateReservationDto dto = new CreateReservationDto();
        dto.setCustomerName("John Doe");
        dto.setPhoneNumber("722666999");
        dto.setStartTime(OffsetDateTime.now().plusHours(1));
        dto.setEndTime(OffsetDateTime.now().plusHours(2));
        dto.setGameType(GameType.DOUBLES);
        dto.setCourtNumber(69);

        CustomerEntity customer = new CustomerEntity();
        customer.setName(dto.getCustomerName());
        customer.setPhoneNumber(dto.getPhoneNumber());

        when(customerService.createIfNotExist(customer)).thenReturn(testCustomerEntity);
        when(courtService.findByCourtNumber(69)).thenReturn(testCourtEntity);

        ReservationEntity entity = mapper.toReservationEntity(dto, customerService, courtService);

        assertThat(entity).isNotNull();
        assertThat(entity.getCustomer().getName()).isEqualTo(dto.getCustomerName());
        assertThat(entity.getCustomer()).isEqualTo(testCustomerEntity);
        assertThat(entity.getEndTime()).isEqualTo(dto.getEndTime());
        assertThat(entity.getGameType()).isEqualTo(dto.getGameType());
        assertThat(entity.getCourt()).isEqualTo(testCourtEntity);
    }

    @Test
    void toDto_nullInput_returnsNull() {
        assertThat(mapper.toDto(null, reservationService)).isNull();
    }

    @Test
    void toEntity_nullInput_returnsNull() {
        assertThat(mapper.toReservationEntity(null, customerService, courtService))
                .isNull();
    }
}
