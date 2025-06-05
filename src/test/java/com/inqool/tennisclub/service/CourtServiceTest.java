package com.inqool.tennisclub.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import com.inqool.tennisclub.data.repository.CourtRepository;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import com.inqool.tennisclub.exceptions.NonUniqueFieldException;
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
public class CourtServiceTest {

    @Mock
    private CourtRepository courtRepository;

    @InjectMocks
    private CourtService courtService;

    private CourtEntity testCourt;
    private CourtEntity secondCourt;
    private CourtSurfaceEntity claySurface;

    @BeforeEach
    void setUp() {
        claySurface = new CourtSurfaceEntity();
        claySurface.setId(1L);
        claySurface.setSurfaceName("Clay");
        claySurface.setSurfaceDescription("Red clay surface");
        claySurface.setCostPerMinute(BigDecimal.valueOf(0.15));
        claySurface.setActive(true);

        testCourt = new CourtEntity();
        testCourt.setId(1L);
        testCourt.setCourtNumber(1);
        testCourt.setCourtSurface(claySurface);
        testCourt.setActive(true);

        secondCourt = new CourtEntity();
        secondCourt.setId(2L);
        secondCourt.setCourtNumber(2);
        secondCourt.setCourtSurface(claySurface);
        secondCourt.setActive(true);
    }

    @Test
    void create_validEntity_returnsCreatedEntity() {
        CourtEntity newCourt = new CourtEntity();
        newCourt.setCourtNumber(3);
        newCourt.setCourtSurface(claySurface);
        newCourt.setActive(true);

        CourtEntity savedCourt = new CourtEntity();
        savedCourt.setId(3L);
        savedCourt.setCourtNumber(3);
        savedCourt.setCourtSurface(claySurface);
        savedCourt.setActive(true);

        when(courtRepository.save(newCourt)).thenReturn(savedCourt);

        CourtEntity result = courtService.create(newCourt);

        assertThat(result).isEqualTo(savedCourt);
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getCourtNumber()).isEqualTo(3);
        verify(courtRepository, times(1)).save(newCourt);
    }

    @Test
    void create_duplicateCourtNumber_throwsNonUniqueFieldException() {
        CourtEntity existingCourt = new CourtEntity();
        existingCourt.setId(1L);
        existingCourt.setCourtNumber(3);
        existingCourt.setCourtSurface(claySurface);
        existingCourt.setActive(true);

        CourtEntity newCourt = new CourtEntity();
        newCourt.setCourtNumber(3);
        newCourt.setCourtSurface(claySurface);
        newCourt.setActive(true);

        when(courtRepository.findByCourtNumber(3)).thenReturn(Optional.of(existingCourt));

        assertThatThrownBy(() -> courtService.create(newCourt))
                .isInstanceOf(NonUniqueFieldException.class)
                .hasMessageContaining("CourtNumber with value 3 already exists");

        verify(courtRepository, never()).save(any());
    }

    @Test
    void findById_existingId_returnsEntity() {
        Long id = 1L;
        when(courtRepository.findById(id)).thenReturn(Optional.of(testCourt));

        CourtEntity result = courtService.findById(id);

        assertThat(result).isEqualTo(testCourt);
        assertThat(result.getCourtNumber()).isEqualTo(1);
        verify(courtRepository, times(1)).findById(id);
    }

    @Test
    void findById_nonExistingId_throwsEntityNotFoundException() {
        Long id = 99L;
        when(courtRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courtService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Court with id " + id + " not found");

        verify(courtRepository, times(1)).findById(id);
    }

    @Test
    void findByCourtNumber_existingNumber_returnsEntity() {
        Integer courtNumber = 1;
        when(courtRepository.findByCourtNumber(courtNumber)).thenReturn(Optional.of(testCourt));

        CourtEntity result = courtService.findByCourtNumber(courtNumber);

        assertThat(result).isEqualTo(testCourt);
        assertThat(result.getCourtNumber()).isEqualTo(courtNumber);
        verify(courtRepository, times(1)).findByCourtNumber(courtNumber);
    }

    @Test
    void findByCourtNumber_nonExistingNumber_throwsEntityNotFoundException() {
        Integer courtNumber = 99;
        when(courtRepository.findByCourtNumber(courtNumber)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courtService.findByCourtNumber(courtNumber))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Court with number " + courtNumber + " not found");

        verify(courtRepository, times(1)).findByCourtNumber(courtNumber);
    }

    @Test
    void findAll_returnsListOfEntities() {
        List<CourtEntity> courts = List.of(testCourt, secondCourt);
        when(courtRepository.findAll()).thenReturn(courts);

        List<CourtEntity> result = courtService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testCourt, secondCourt);
        verify(courtRepository, times(1)).findAll();
    }

    @Test
    void findAll_emptyRepository_returnsEmptyList() {
        List<CourtEntity> courts = List.of();
        when(courtRepository.findAll()).thenReturn(courts);

        List<CourtEntity> result = courtService.findAll();

        assertThat(result).isEmpty();
        verify(courtRepository, times(1)).findAll();
    }

    @Test
    void update_existingEntity_returnsUpdatedEntity() {
        CourtEntity courtToUpdate = new CourtEntity();
        courtToUpdate.setId(1L);
        courtToUpdate.setCourtNumber(1);
        courtToUpdate.setCourtSurface(claySurface);
        courtToUpdate.setActive(false);

        when(courtRepository.findById(1L)).thenReturn(Optional.of(testCourt));
        when(courtRepository.save(courtToUpdate)).thenReturn(courtToUpdate);

        CourtEntity result = courtService.update(courtToUpdate);

        assertThat(result).isEqualTo(courtToUpdate);
        assertThat(result.getCourtNumber()).isEqualTo(1);
        assertThat(result.isActive()).isFalse();
        verify(courtRepository, times(1)).findById(1L);
        verify(courtRepository, times(1)).save(courtToUpdate);
    }

    @Test
    void update_nonExistingEntity_throwsEntityNotFoundException() {
        CourtEntity courtToUpdate = new CourtEntity();
        courtToUpdate.setId(99L);
        courtToUpdate.setCourtNumber(99);

        when(courtRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courtService.update(courtToUpdate))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Court with id " + 99L + " not found");

        verify(courtRepository, times(1)).findById(99L);
        verify(courtRepository, never()).save(any());
    }

    @Test
    void deleteById_existingId_deletesEntity() {
        Long id = 1L;
        when(courtRepository.existsById(id)).thenReturn(true);
        doNothing().when(courtRepository).deleteById(id);

        courtService.deleteById(id);

        verify(courtRepository, times(1)).existsById(id);
        verify(courtRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteById_nonExistingId_throwsEntityNotFoundException() {
        Long id = 99L;
        when(courtRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> courtService.deleteById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Court with id " + id + " not found");

        verify(courtRepository, times(1)).existsById(id);
        verify(courtRepository, never()).deleteById(any());
    }
}
