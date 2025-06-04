package com.inqool.tennisclub.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import com.inqool.tennisclub.data.repository.CourtSurfaceRepository;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
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
public class CourtSurfaceServiceTest {

    @Mock
    private CourtSurfaceRepository courtSurfaceRepository;

    @InjectMocks
    private CourtSurfaceService courtSurfaceService;

    private CourtSurfaceEntity testEntity;
    private CourtSurfaceEntity grassEntity;

    @BeforeEach
    void setUp() {
        testEntity = new CourtSurfaceEntity();
        testEntity.setId(1L);
        testEntity.setSurfaceName("Clay");
        testEntity.setSurfaceDescription("Red clay surface");
        testEntity.setCostPerMinute(BigDecimal.valueOf(0.15));
        testEntity.setActive(true);

        grassEntity = new CourtSurfaceEntity();
        grassEntity.setId(2L);
        grassEntity.setSurfaceName("Grass");
        grassEntity.setSurfaceDescription("Natural grass");
        grassEntity.setCostPerMinute(BigDecimal.valueOf(0.25));
        grassEntity.setActive(true);
    }

    @Test
    void create_validEntity_returnsCreatedEntity() {
        CourtSurfaceEntity newEntity = new CourtSurfaceEntity();
        newEntity.setSurfaceName("Hard Court");
        newEntity.setSurfaceDescription("Synthetic surface");
        newEntity.setCostPerMinute(BigDecimal.valueOf(0.20));

        CourtSurfaceEntity savedEntity = new CourtSurfaceEntity();
        savedEntity.setId(3L);
        savedEntity.setSurfaceName("Hard Court");
        savedEntity.setSurfaceDescription("Synthetic surface");
        savedEntity.setCostPerMinute(BigDecimal.valueOf(0.20));
        savedEntity.setActive(true);

        when(courtSurfaceRepository.save(newEntity)).thenReturn(savedEntity);

        CourtSurfaceEntity result = courtSurfaceService.create(newEntity);

        assertThat(result).isEqualTo(savedEntity);
        assertThat(result.getId()).isEqualTo(3L);
        verify(courtSurfaceRepository, times(1)).save(newEntity);
    }

    @Test
    void findById_existingId_returnsEntity() {
        Long id = 1L;
        when(courtSurfaceRepository.findById(id)).thenReturn(Optional.of(testEntity));

        CourtSurfaceEntity result = courtSurfaceService.findById(id);

        assertThat(result).isEqualTo(testEntity);
        verify(courtSurfaceRepository, times(1)).findById(id);
    }

    @Test
    void findById_nonExistingId_throwsEntityNotFoundException() {
        Long id = 99L;
        when(courtSurfaceRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courtSurfaceService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("CourtSurface with id " + id + " not found");

        verify(courtSurfaceRepository, times(1)).findById(id);
    }

    @Test
    void findAll_returnsListOfEntities() {
        List<CourtSurfaceEntity> entities = List.of(testEntity, grassEntity);
        when(courtSurfaceRepository.findAll()).thenReturn(entities);

        List<CourtSurfaceEntity> result = courtSurfaceService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testEntity, grassEntity);
        verify(courtSurfaceRepository, times(1)).findAll();
    }

    @Test
    void findAll_emptyRepository_returnsEmptyList() {
        List<CourtSurfaceEntity> entities = List.of();
        when(courtSurfaceRepository.findAll()).thenReturn(entities);

        List<CourtSurfaceEntity> result = courtSurfaceService.findAll();

        assertThat(result).isEmpty();
        verify(courtSurfaceRepository, times(1)).findAll();
    }

    @Test
    void update_existingEntity_returnsUpdatedEntity() {
        CourtSurfaceEntity entityToUpdate = new CourtSurfaceEntity();
        entityToUpdate.setId(1L);
        entityToUpdate.setSurfaceName("Updated Clay");
        entityToUpdate.setSurfaceDescription("Updated description");
        entityToUpdate.setCostPerMinute(BigDecimal.valueOf(0.18));
        entityToUpdate.setActive(true);

        when(courtSurfaceRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(courtSurfaceRepository.save(entityToUpdate)).thenReturn(entityToUpdate);

        CourtSurfaceEntity result = courtSurfaceService.update(entityToUpdate);

        assertThat(result).isEqualTo(entityToUpdate);
        assertThat(result.getSurfaceName()).isEqualTo("Updated Clay");
        assertThat(result.getSurfaceDescription()).isEqualTo("Updated description");
        assertThat(result.getCostPerMinute()).isEqualTo(BigDecimal.valueOf(0.18));
        verify(courtSurfaceRepository, times(1)).findById(1L);
        verify(courtSurfaceRepository, times(1)).save(entityToUpdate);
    }

    @Test
    void update_nonExistingEntity_throwsEntityNotFoundException() {
        CourtSurfaceEntity entityToUpdate = new CourtSurfaceEntity();
        entityToUpdate.setId(99L);
        entityToUpdate.setSurfaceName("Non-existing");

        when(courtSurfaceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courtSurfaceService.update(entityToUpdate))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("CourtSurface with id " + 99L + " not found");

        verify(courtSurfaceRepository, times(1)).findById(99L);
        verify(courtSurfaceRepository, never()).save(any());
    }

    @Test
    void deleteById_existingId_deletesEntity() {
        Long id = 1L;
        when(courtSurfaceRepository.existsById(id)).thenReturn(true);
        doNothing().when(courtSurfaceRepository).deleteById(id);

        courtSurfaceService.deleteById(id);

        verify(courtSurfaceRepository, times(1)).existsById(id);
        verify(courtSurfaceRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteById_nonExistingId_throwsEntityNotFoundException() {
        Long id = 99L;
        when(courtSurfaceRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> courtSurfaceService.deleteById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("CourtSurface with id " + id + " not found");

        verify(courtSurfaceRepository, times(1)).existsById(id);
        verify(courtSurfaceRepository, never()).deleteById(any());
    }

    @Test
    void create_entityWithNullValues_handlesGracefully() {
        CourtSurfaceEntity entityWithNulls = new CourtSurfaceEntity();
        entityWithNulls.setSurfaceName("Test");
        entityWithNulls.setSurfaceDescription(null); // nullable field
        entityWithNulls.setCostPerMinute(BigDecimal.valueOf(0.10));

        CourtSurfaceEntity savedEntity = new CourtSurfaceEntity();
        savedEntity.setId(4L);
        savedEntity.setSurfaceName("Test");
        savedEntity.setSurfaceDescription(null);
        savedEntity.setCostPerMinute(BigDecimal.valueOf(0.10));
        savedEntity.setActive(true);

        when(courtSurfaceRepository.save(entityWithNulls)).thenReturn(savedEntity);

        CourtSurfaceEntity result = courtSurfaceService.create(entityWithNulls);

        assertThat(result).isEqualTo(savedEntity);
        assertThat(result.getSurfaceDescription()).isNull();
        verify(courtSurfaceRepository, times(1)).save(entityWithNulls);
    }
}
