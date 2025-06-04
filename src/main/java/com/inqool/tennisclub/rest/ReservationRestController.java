package com.inqool.tennisclub.rest;

import com.inqool.tennisclub.api.CreateReservationDto;
import com.inqool.tennisclub.api.ReservationDto;
import com.inqool.tennisclub.facade.ReservationFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tags(value = @Tag(name = "Reservation API"))
@RestController
@RequestMapping("/api/reservation")
public class ReservationRestController {
    private final ReservationFacade reservationFacade;

    public ReservationRestController(ReservationFacade reservationFacade) {
        this.reservationFacade = reservationFacade;
    }

    @Operation(summary = "Create a new Reservation")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reservation created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid Reservation data provided", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ReservationDto> create(@RequestBody @Valid CreateReservationDto dto) {
        ReservationDto created = reservationFacade.create(dto);
        return ResponseEntity.created(URI.create("/reservation/" + created.getId()))
                .body(created);
    }

    @Operation(summary = "Get all Reservations")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "List of Reservations retrieved successfully")})
    @GetMapping
    public ResponseEntity<List<ReservationDto>> findAll() {
        return ResponseEntity.ok(reservationFacade.findAll());
    }

    @Operation(summary = "Get a Reservations by court number")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reservations found"),
        @ApiResponse(responseCode = "404", description = "Court number not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationFacade.findById(id));
    }

    @Operation(summary = "Get a Reservations by court number")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reservations found"),
        @ApiResponse(responseCode = "404", description = "Court number not found", content = @Content)
    })
    @GetMapping("/court/{number}")
    public ResponseEntity<List<ReservationDto>> findByCourt(@PathVariable Integer number) {
        return ResponseEntity.ok(reservationFacade.findByCourt(number));
    }

    @Operation(summary = "Get a Reservations by phone number")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reservations found"),
        @ApiResponse(responseCode = "404", description = "Phone number not found", content = @Content)
    })
    @GetMapping("/phone/{number}")
    public ResponseEntity<List<ReservationDto>> findByPhone(
            @PathVariable String number, @RequestParam(required = false, defaultValue = "false") boolean futureOnly) {
        return ResponseEntity.ok(reservationFacade.findByPhone(number, futureOnly));
    }

    @Operation(summary = "Update a Reservation")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reservation updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid Reservation data provided", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> update(@PathVariable Long id, @RequestBody @Valid CreateReservationDto dto) {
        ReservationDto updated = reservationFacade.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a Reservation")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reservation deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
