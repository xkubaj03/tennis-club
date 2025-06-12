package com.inqool.tennisclub.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inqool.tennisclub.api.court.CourtDto;
import com.inqool.tennisclub.api.court.CreateCourtDto;
import com.inqool.tennisclub.exceptions.EntityNotFoundException;
import com.inqool.tennisclub.facade.CourtFacade;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class CourtControllerTest {
    @Mock
    private CourtFacade courtFacade;

    @InjectMocks
    private CourtRestController courtRestController;

    @Test
    void create_validCourtDto_returnsCreatedCourt() {
        CreateCourtDto createDto = new CreateCourtDto(1, 1L);
        CourtDto expectedDto = new CourtDto(1L, 1, 1L);
        when(courtFacade.create(createDto)).thenReturn(expectedDto);

        ResponseEntity<CourtDto> response = courtRestController.create(createDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedDto);
        verify(courtFacade, times(1)).create(createDto);
    }

    @Test
    void findAll_returnsListOfCourts() {
        List<CourtDto> expectedList = List.of(new CourtDto(1L, 1, 1L), new CourtDto(2L, 2, 1L));
        when(courtFacade.findAll()).thenReturn(expectedList);

        ResponseEntity<List<CourtDto>> response = courtRestController.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedList);
        verify(courtFacade, times(1)).findAll();
    }

    @Test
    void findById_existingId_returnsCourt() {
        Long id = 1L;
        CourtDto expectedDto = new CourtDto(1L, 1, 1L);
        when(courtFacade.findById(id)).thenReturn(expectedDto);

        ResponseEntity<CourtDto> response = courtRestController.findById(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedDto);
    }

    @Test
    void findById_nonExistingId_throwsException() {
        Long id = 99L;
        when(courtFacade.findById(id)).thenThrow(new EntityNotFoundException("Court with id " + id + " not found"));

        assertThrows(EntityNotFoundException.class, () -> courtRestController.findById(id));
    }

    @Test
    void update_existingId_updatesCourt() {
        Long id = 1L;
        CreateCourtDto updateDto = new CreateCourtDto(2, 2L);
        CourtDto updatedDto = new CourtDto(1L, 2, 2L);
        when(courtFacade.update(id, updateDto)).thenReturn(updatedDto);

        ResponseEntity<CourtDto> response = courtRestController.update(id, updateDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedDto);
        verify(courtFacade, times(1)).update(id, updateDto);
    }

    @Test
    void delete_existingId_deletesCourt() {
        Long id = 1L;
        doNothing().when(courtFacade).deleteById(id);

        ResponseEntity<Void> response = courtRestController.delete(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(courtFacade, times(1)).deleteById(id);
    }
}
