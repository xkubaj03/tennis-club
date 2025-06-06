package com.inqool.tennisclub.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.inqool.tennisclub.api.CourtDto;
import com.inqool.tennisclub.api.CreateCourtDto;
import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import com.inqool.tennisclub.mappers.CourtMapper;
import com.inqool.tennisclub.service.CourtService;
import com.inqool.tennisclub.service.CourtSurfaceService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CourtFacadeTest {

    @Mock
    private CourtService courtService;

    @Mock
    private CourtMapper courtMapper;

    @Mock
    private CourtSurfaceService courtSurfaceService;

    @InjectMocks
    private CourtFacade courtFacade;

    private CourtEntity testEntity;
    private CourtDto testDto;
    private CreateCourtDto createDto;

    @BeforeEach
    void setUp() {
        CourtSurfaceEntity testSurfaceEntity = new CourtSurfaceEntity();
        testSurfaceEntity.setId(1L);
        testSurfaceEntity.setSurfaceName("Clay");
        testSurfaceEntity.setSurfaceDescription("Red clay surface");
        testSurfaceEntity.setCostPerMinute(BigDecimal.valueOf(0.15));
        testSurfaceEntity.setActive(true);

        testEntity = new CourtEntity();
        testEntity.setId(1L);
        testEntity.setCourtNumber(1);
        testEntity.setCourtSurface(testSurfaceEntity);
        testEntity.setActive(true);

        testDto = new CourtDto(1L, 1, 1L);

        createDto = new CreateCourtDto(1, 1L);
    }

    @Test
    void create_validCreateCourtDto_returnsCreatedCourt() {
        when(courtMapper.toEntity(createDto, courtSurfaceService)).thenReturn(testEntity);
        when(courtService.create(testEntity)).thenReturn(testEntity);
        when(courtMapper.toDto(testEntity)).thenReturn(testDto);

        CourtDto result = courtFacade.create(createDto);

        assertThat(result).isEqualTo(testDto);
        verify(courtMapper, times(1)).toEntity(createDto, courtSurfaceService);
        verify(courtService, times(1)).create(testEntity);
        verify(courtMapper, times(1)).toDto(testEntity);
    }

    @Test
    void findById_existingId_returnsCourt() {
        Long id = 1L;
        when(courtService.findById(id)).thenReturn(testEntity);
        when(courtMapper.toDto(testEntity)).thenReturn(testDto);

        CourtDto result = courtFacade.findById(id);

        assertThat(result).isEqualTo(testDto);
        verify(courtService, times(1)).findById(id);
        verify(courtMapper, times(1)).toDto(testEntity);
    }

    @Test
    void findById_nonExistingId_throwsException() {
        Long id = 99L;
        when(courtService.findById(id)).thenThrow(new EntityNotFoundException("Court not found"));

        assertThrows(EntityNotFoundException.class, () -> courtFacade.findById(id));

        verify(courtService, times(1)).findById(id);
        verify(courtMapper, never()).toDto(any());
    }

    @Test
    void findAll_returnsListOfCourts() {
        CourtSurfaceEntity grassSurfaceEntity = new CourtSurfaceEntity();
        grassSurfaceEntity.setId(2L);
        grassSurfaceEntity.setSurfaceName("Grass");
        grassSurfaceEntity.setSurfaceDescription("Natural grass");
        grassSurfaceEntity.setCostPerMinute(BigDecimal.valueOf(0.25));
        grassSurfaceEntity.setActive(true);

        CourtEntity grassCourtEntity = new CourtEntity();
        grassCourtEntity.setId(2L);
        grassCourtEntity.setCourtNumber(2);
        grassCourtEntity.setCourtSurface(grassSurfaceEntity);
        grassCourtEntity.setActive(true);

        CourtDto grassCourtDto = new CourtDto(2L, 2, 2L);

        List<CourtEntity> entities = List.of(testEntity, grassCourtEntity);
        List<CourtDto> dtos = List.of(testDto, grassCourtDto);

        when(courtService.findAll()).thenReturn(entities);
        when(courtMapper.toDtoList(entities)).thenReturn(dtos);

        List<CourtDto> result = courtFacade.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(dtos);
        verify(courtService, times(1)).findAll();
        verify(courtMapper, times(1)).toDtoList(entities);
    }

    @Test
    void findAll_emptyList_returnsEmptyList() {
        List<CourtEntity> entities = List.of();
        List<CourtDto> dtos = List.of();

        when(courtService.findAll()).thenReturn(entities);
        when(courtMapper.toDtoList(entities)).thenReturn(dtos);

        List<CourtDto> result = courtFacade.findAll();

        assertThat(result).isEmpty();
        verify(courtService, times(1)).findAll();
        verify(courtMapper, times(1)).toDtoList(entities);
    }

    @Test
    void update_existingCourt_updatesCourt() {
        Long id = 1L;
        CreateCourtDto updateDto = new CreateCourtDto(3, 2L);

        CourtSurfaceEntity hardSurfaceEntity = new CourtSurfaceEntity();
        hardSurfaceEntity.setId(2L);
        hardSurfaceEntity.setSurfaceName("Hard Court");
        hardSurfaceEntity.setSurfaceDescription("Synthetic hard surface");
        hardSurfaceEntity.setCostPerMinute(BigDecimal.valueOf(0.20));
        hardSurfaceEntity.setActive(true);

        CourtEntity mappedEntity = new CourtEntity();
        mappedEntity.setCourtNumber(3);
        mappedEntity.setCourtSurface(hardSurfaceEntity);
        mappedEntity.setActive(true);

        CourtEntity entityWithId = new CourtEntity();
        entityWithId.setId(id);
        entityWithId.setCourtNumber(3);
        entityWithId.setCourtSurface(hardSurfaceEntity);
        entityWithId.setActive(true);

        CourtEntity updatedEntity = new CourtEntity();
        updatedEntity.setId(id);
        updatedEntity.setCourtNumber(3);
        updatedEntity.setCourtSurface(hardSurfaceEntity);
        updatedEntity.setActive(true);

        CourtDto updatedDto = new CourtDto(id, 3, 2L);

        when(courtMapper.toEntity(updateDto, courtSurfaceService)).thenReturn(mappedEntity);
        when(courtService.update(any(CourtEntity.class))).thenReturn(updatedEntity);
        when(courtMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        CourtDto result = courtFacade.update(id, updateDto);

        assertThat(result).isEqualTo(updatedDto);
        verify(courtMapper, times(1)).toEntity(updateDto, courtSurfaceService);
        verify(courtService, times(1))
                .update(argThat(entity ->
                        entity.getId().equals(id) && entity.getCourtNumber().equals(3)));
        verify(courtMapper, times(1)).toDto(updatedEntity);
    }

    @Test
    void update_nonExistingCourt_throwsException() {
        Long id = 99L;
        CreateCourtDto updateDto = new CreateCourtDto(3, 2L);

        CourtEntity mappedEntity = new CourtEntity();
        mappedEntity.setCourtNumber(3);
        mappedEntity.setActive(true);

        when(courtMapper.toEntity(updateDto, courtSurfaceService)).thenReturn(mappedEntity);
        when(courtService.update(any(CourtEntity.class))).thenThrow(new EntityNotFoundException("Court not found"));

        assertThrows(EntityNotFoundException.class, () -> courtFacade.update(id, updateDto));

        verify(courtMapper, times(1)).toEntity(updateDto, courtSurfaceService);
        verify(courtService, times(1)).update(any(CourtEntity.class));
        verify(courtMapper, never()).toDto(any());
    }

    @Test
    void deleteById_existingCourt_deletesCourt() {
        Long id = 1L;
        doNothing().when(courtService).deleteById(id);

        courtFacade.deleteById(id);

        verify(courtService, times(1)).deleteById(id);
    }

    @Test
    void deleteById_nonExistingCourt_callsServiceDelete() {
        Long id = 99L;
        doNothing().when(courtService).deleteById(id);

        courtFacade.deleteById(id);

        verify(courtService, times(1)).deleteById(id);
    }
}
