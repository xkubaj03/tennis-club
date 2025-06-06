package com.inqool.tennisclub.mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.inqool.tennisclub.api.CourtDto;
import com.inqool.tennisclub.api.CreateCourtDto;
import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import com.inqool.tennisclub.service.CourtSurfaceService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CourtMapperTest {

    @Mock
    private CourtSurfaceService courtSurfaceService;

    private final CourtMapper mapper = Mappers.getMapper(CourtMapper.class);

    private CourtSurfaceEntity testCourtSurfaceEntity;

    private CourtEntity testCourtEntity;

    @BeforeEach
    void setUp() {
        testCourtSurfaceEntity = new CourtSurfaceEntity();
        testCourtSurfaceEntity.setId(1L);
        testCourtSurfaceEntity.setSurfaceName("Clay");
        testCourtSurfaceEntity.setSurfaceDescription("Red clay surface");
        testCourtSurfaceEntity.setCostPerMinute(BigDecimal.valueOf(0.15));

        testCourtEntity = new CourtEntity();
        testCourtEntity.setId(1L);
        testCourtEntity.setCourtNumber(69);
        testCourtEntity.setCourtSurface(testCourtSurfaceEntity);
    }

    @Test
    void toDto_validEntity_returnsMappedDto() {
        CourtEntity entity = new CourtEntity();
        entity.setId(2L);
        entity.setCourtNumber(11);
        entity.setCourtSurface(testCourtSurfaceEntity);

        CourtDto dto = mapper.toDto(entity);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getCourtNumber()).isEqualTo(11);
        assertThat(dto.getSurfaceId()).isEqualTo(testCourtSurfaceEntity.getId());
    }

    @Test
    void toDtoList_validEntities_returnsMappedDtoList() {
        CourtEntity courtEntity = new CourtEntity();
        courtEntity.setId(2L);
        courtEntity.setCourtNumber(11);
        courtEntity.setCourtSurface(testCourtSurfaceEntity);

        List<CourtDto> dtos = mapper.toDtoList(List.of(testCourtEntity, courtEntity));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getId()).isEqualTo(1L);
        assertThat(dtos.get(0).getCourtNumber()).isEqualTo(69);
        assertThat(dtos.get(0).getSurfaceId()).isEqualTo(1L);
        assertThat(dtos.get(1).getId()).isEqualTo(2L);
        assertThat(dtos.get(1).getCourtNumber()).isEqualTo(11);
        assertThat(dtos.get(1).getSurfaceId()).isEqualTo(1L);
    }

    @Test
    void toEntity_validCreateDto_returnsMappedEntity() {
        CreateCourtDto dto = new CreateCourtDto();
        dto.setCourtNumber(11);
        dto.setSurfaceId(1L);
        when(courtSurfaceService.findById(1L)).thenReturn(testCourtSurfaceEntity);

        CourtEntity entity = mapper.toEntity(dto, courtSurfaceService);

        assertThat(entity).isNotNull();
        assertThat(entity.getCourtNumber()).isEqualTo(11);
        assertThat(entity.getCourtSurface()).isEqualTo(testCourtSurfaceEntity);
    }

    @Test
    void toDto_nullInput_returnsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toEntity_nullInput_returnsNull() {
        assertThat(mapper.toEntity(null, courtSurfaceService)).isNull();
    }
}
