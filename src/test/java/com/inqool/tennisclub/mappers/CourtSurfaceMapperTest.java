package com.inqool.tennisclub.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import com.inqool.tennisclub.api.CourtSurfaceDto;
import com.inqool.tennisclub.api.CreateCourtSurfaceDto;
import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CourtSurfaceMapperImpl.class})
class CourtSurfaceMapperTest {

    @Autowired
    private CourtSurfaceMapper mapper;

    @Test
    void toDto_MapEntityToDto() {
        CourtSurfaceEntity entity = new CourtSurfaceEntity();
        entity.setId(1L);
        entity.setSurfaceName("Clay");
        entity.setSurfaceDescription("Some description");
        entity.setCostPerMinute(BigDecimal.valueOf(1.5));

        CourtSurfaceDto dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getSurfaceName()).isEqualTo("Clay");
        assertThat(dto.getSurfaceDescription()).isEqualTo("Some description");
        assertThat(dto.getCostPerMinute()).isEqualTo(BigDecimal.valueOf(1.5));
    }

    @Test
    void toDtoList_MapEntitiesToDtos() {
        CourtSurfaceEntity entity1 = new CourtSurfaceEntity();
        entity1.setId(1L);
        entity1.setSurfaceName("Clay");
        entity1.setSurfaceDescription("Somesome description");
        entity1.setCostPerMinute(BigDecimal.valueOf(2.5));

        CourtSurfaceEntity entity2 = new CourtSurfaceEntity();
        entity2.setId(2L);
        entity2.setSurfaceName("Grass");
        entity2.setSurfaceDescription("Some different description");
        entity2.setCostPerMinute(BigDecimal.valueOf(3.5));

        List<CourtSurfaceDto> dtos = mapper.toDtoList(List.of(entity1, entity2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getSurfaceName()).isEqualTo("Clay");
        assertThat(dtos.get(0).getSurfaceDescription()).isEqualTo("Somesome description");
        assertThat(dtos.get(0).getCostPerMinute()).isEqualTo(BigDecimal.valueOf(2.5));
        assertThat(dtos.get(1).getSurfaceName()).isEqualTo("Grass");
        assertThat(dtos.get(1).getSurfaceDescription()).isEqualTo("Some different description");
        assertThat(dtos.get(1).getCostPerMinute()).isEqualTo(BigDecimal.valueOf(3.5));
    }

    @Test
    void toEntity_MapCreateDtoToEntityWithoutId() {
        CreateCourtSurfaceDto createDto = new CreateCourtSurfaceDto();
        createDto.setSurfaceName("Hard");
        createDto.setCostPerMinute(0.5);

        CourtSurfaceEntity entity = mapper.toEntity(createDto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getSurfaceName()).isEqualTo("Hard");
        assertThat(entity.getCostPerMinute()).isEqualTo(BigDecimal.valueOf(0.5));
    }
}
