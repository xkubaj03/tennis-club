package com.inqool.tennisclub.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inqool.tennisclub.api.CourtSurfaceDto;
import com.inqool.tennisclub.api.CreateCourtSurfaceDto;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import com.inqool.tennisclub.facade.CourtSurfaceFacade;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class CourtSurfaceControllerTest {
    @Mock
    private CourtSurfaceFacade courtSurfaceFacade;

    @InjectMocks
    private CourtSurfaceRestController courtSurfaceRestController;

    @Test
    void create_validCourtSurfaceDto_returnsCreatedCourtSurface() {
        CreateCourtSurfaceDto createDto = new CreateCourtSurfaceDto("Clay", "Dark clay", 0.1);
        CourtSurfaceDto expectedDto = new CourtSurfaceDto(1L, "Clay", "Dark clay", BigDecimal.valueOf(0.1));
        when(courtSurfaceFacade.create(createDto)).thenReturn(expectedDto);

        ResponseEntity<CourtSurfaceDto> response = courtSurfaceRestController.create(createDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedDto);
        verify(courtSurfaceFacade, times(1)).create(createDto);
    }

    @Test
    void findAll_returnsListOfCourtSurfaces() {
        List<CourtSurfaceDto> expectedList =
                List.of(new CourtSurfaceDto(1L, "Clay", "Dark clay", BigDecimal.valueOf(0.1)));
        when(courtSurfaceFacade.findAll()).thenReturn(expectedList);

        ResponseEntity<List<CourtSurfaceDto>> response = courtSurfaceRestController.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedList);
        verify(courtSurfaceFacade, times(1)).findAll();
    }

    @Test
    void findById_existingId_returnsCourtSurface() {
        Long id = 1L;
        CourtSurfaceDto expectedDto = new CourtSurfaceDto(1L, "Clay", "Dark clay", BigDecimal.valueOf(0.1));

        when(courtSurfaceFacade.findById(id)).thenReturn(expectedDto);

        ResponseEntity<CourtSurfaceDto> response = courtSurfaceRestController.findById(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedDto);
    }

    @Test
    void findById_nonExistingId_throwsException() {
        Long id = 99L;
        when(courtSurfaceFacade.findById(id))
                .thenThrow(new EntityNotFoundException("CourtSurface with id " + id + " not found"));

        assertThrows(EntityNotFoundException.class, () -> courtSurfaceRestController.findById(id));
    }

    @Test
    void update_existingId_updatesCourtSurface() {
        Long id = 1L;
        CreateCourtSurfaceDto updateDto = new CreateCourtSurfaceDto("Grass", "Short grass", 0.1);
        CourtSurfaceDto updatedDto = new CourtSurfaceDto(1L, "Clay", "Dark clay", BigDecimal.valueOf(0.1));
        when(courtSurfaceFacade.update(id, updateDto)).thenReturn(updatedDto);

        ResponseEntity<CourtSurfaceDto> response = courtSurfaceRestController.update(id, updateDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedDto);
        verify(courtSurfaceFacade, times(1)).update(id, updateDto);
    }

    @Test
    void delete_existingId_deletesCourtSurface() {
        Long id = 1L;
        doNothing().when(courtSurfaceFacade).deleteById(id);

        ResponseEntity<Void> response = courtSurfaceRestController.delete(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(courtSurfaceFacade, times(1)).deleteById(id);
    }
}
