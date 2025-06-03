package com.inqool.tennisclub.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.inqool.tennisclub.api.CourtSurfaceDto;
import com.inqool.tennisclub.api.CreateCourtSurfaceDto;
import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import com.inqool.tennisclub.mappers.CourtSurfaceMapper;
import com.inqool.tennisclub.service.CourtSurfaceService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CourtSurfaceFacadeTest {

    @Mock
    private CourtSurfaceService courtSurfaceService;

    @Mock
    private CourtSurfaceMapper courtSurfaceMapper;

    @InjectMocks
    private CourtSurfaceFacade courtSurfaceFacade;

    private CourtSurfaceEntity testEntity;
    private CourtSurfaceDto testDto;
    private CreateCourtSurfaceDto createDto;

    @BeforeEach
    void setUp() {
        testEntity = new CourtSurfaceEntity();
        testEntity.setId(1L);
        testEntity.setSurfaceName("Clay");
        testEntity.setSurfaceDescription("Red clay surface");
        testEntity.setCostPerMinute(BigDecimal.valueOf(0.15));
        testEntity.setActive(true);

        testDto = new CourtSurfaceDto(1L, "Clay", "Red clay surface", BigDecimal.valueOf(0.15));

        createDto = new CreateCourtSurfaceDto("Clay", "Red clay surface", 0.15);
    }

    @Test
    void create_validCourtSurfaceDto_returnsCreatedCourtSurface() {
        when(courtSurfaceMapper.toEntity(createDto)).thenReturn(testEntity);
        when(courtSurfaceService.create(testEntity)).thenReturn(testEntity);
        when(courtSurfaceMapper.toDto(testEntity)).thenReturn(testDto);

        CourtSurfaceDto result = courtSurfaceFacade.create(createDto);

        assertThat(result).isEqualTo(testDto);
        verify(courtSurfaceMapper, times(1)).toEntity(createDto);
        verify(courtSurfaceService, times(1)).create(testEntity);
        verify(courtSurfaceMapper, times(1)).toDto(testEntity);
    }

    @Test
    void findById_existingId_returnsCourtSurface() {
        Long id = 1L;
        when(courtSurfaceService.findById(id)).thenReturn(Optional.of(testEntity));
        when(courtSurfaceMapper.toDto(testEntity)).thenReturn(testDto);

        Optional<CourtSurfaceDto> result = courtSurfaceFacade.findById(id);

        assertThat(result).isPresent().contains(testDto);
        verify(courtSurfaceService, times(1)).findById(id);
        verify(courtSurfaceMapper, times(1)).toDto(testEntity);
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Long id = 99L;
        when(courtSurfaceService.findById(id)).thenReturn(Optional.empty());

        Optional<CourtSurfaceDto> result = courtSurfaceFacade.findById(id);

        assertThat(result).isEmpty();
        verify(courtSurfaceService, times(1)).findById(id);
        verify(courtSurfaceMapper, never()).toDto(any());
    }

    @Test
    void findAll_returnsListOfCourtSurfaces() {
        CourtSurfaceEntity grassEntity = new CourtSurfaceEntity();
        grassEntity.setId(2L);
        grassEntity.setSurfaceName("Grass");
        grassEntity.setSurfaceDescription("Natural grass");
        grassEntity.setCostPerMinute(BigDecimal.valueOf(0.25));
        grassEntity.setActive(true);

        CourtSurfaceDto grassDto = new CourtSurfaceDto(2L, "Grass", "Natural grass", BigDecimal.valueOf(0.25));

        List<CourtSurfaceEntity> entities = List.of(testEntity, grassEntity);
        List<CourtSurfaceDto> dtos = List.of(testDto, grassDto);

        when(courtSurfaceService.findAll()).thenReturn(entities);
        when(courtSurfaceMapper.toDtoList(entities)).thenReturn(dtos);

        List<CourtSurfaceDto> result = courtSurfaceFacade.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(dtos);
        verify(courtSurfaceService, times(1)).findAll();
        verify(courtSurfaceMapper, times(1)).toDtoList(entities);
    }

    @Test
    void findAll_emptyList_returnsEmptyList() {
        List<CourtSurfaceEntity> entities = List.of();
        List<CourtSurfaceDto> dtos = List.of();

        when(courtSurfaceService.findAll()).thenReturn(entities);
        when(courtSurfaceMapper.toDtoList(entities)).thenReturn(dtos);

        List<CourtSurfaceDto> result = courtSurfaceFacade.findAll();

        assertThat(result).isEmpty();
        verify(courtSurfaceService, times(1)).findAll();
        verify(courtSurfaceMapper, times(1)).toDtoList(entities);
    }

    @Test
    void update_existingCourtSurface_updatesCourtSurface() {
        Long id = 1L;
        CreateCourtSurfaceDto updateDto = new CreateCourtSurfaceDto("Hard Court", "Synthetic hard surface", 0.20);

        CourtSurfaceEntity mappedEntity = new CourtSurfaceEntity();
        mappedEntity.setSurfaceName("Hard Court");
        mappedEntity.setSurfaceDescription("Synthetic hard surface");
        mappedEntity.setCostPerMinute(BigDecimal.valueOf(0.20));

        CourtSurfaceEntity entityWithId = new CourtSurfaceEntity();
        entityWithId.setId(id);
        entityWithId.setSurfaceName("Hard Court");
        entityWithId.setSurfaceDescription("Synthetic hard surface");
        entityWithId.setCostPerMinute(BigDecimal.valueOf(0.20));

        CourtSurfaceEntity updatedEntity = new CourtSurfaceEntity();
        updatedEntity.setId(id);
        updatedEntity.setSurfaceName("Hard Court");
        updatedEntity.setSurfaceDescription("Synthetic hard surface");
        updatedEntity.setCostPerMinute(BigDecimal.valueOf(0.20));
        updatedEntity.setActive(true);

        CourtSurfaceDto updatedDto =
                new CourtSurfaceDto(id, "Hard Court", "Synthetic hard surface", BigDecimal.valueOf(0.20));

        when(courtSurfaceMapper.toEntity(updateDto)).thenReturn(mappedEntity);
        when(courtSurfaceService.update(any(CourtSurfaceEntity.class))).thenReturn(updatedEntity);
        when(courtSurfaceMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        CourtSurfaceDto result = courtSurfaceFacade.update(id, updateDto);

        assertThat(result).isEqualTo(updatedDto);
        verify(courtSurfaceMapper, times(1)).toEntity(updateDto);
        verify(courtSurfaceService, times(1))
                .update(argThat(entity -> entity.getId().equals(id)
                        && entity.getSurfaceName().equals("Hard Court")
                        && entity.getSurfaceDescription().equals("Synthetic hard surface")
                        && entity.getCostPerMinute().equals(BigDecimal.valueOf(0.20))));
        verify(courtSurfaceMapper, times(1)).toDto(updatedEntity);
    }

    @Test
    void deleteById_existingCourtSurface_deletesCourtSurface() {
        Long id = 1L;
        doNothing().when(courtSurfaceService).deleteById(id);

        courtSurfaceFacade.deleteById(id);

        verify(courtSurfaceService, times(1)).deleteById(id);
    }

    @Test
    void deleteById_nonExistingCourtSurface_callsServiceDelete() {
        Long id = 99L;
        doNothing().when(courtSurfaceService).deleteById(id);

        courtSurfaceFacade.deleteById(id);

        verify(courtSurfaceService, times(1)).deleteById(id);
    }
}
