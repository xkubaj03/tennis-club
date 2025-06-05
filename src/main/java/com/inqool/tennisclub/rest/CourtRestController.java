package com.inqool.tennisclub.rest;

import com.inqool.tennisclub.api.CourtDto;
import com.inqool.tennisclub.api.CreateCourtDto;
import com.inqool.tennisclub.facade.CourtFacade;
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

@Tags(value = @Tag(name = "Court API"))
@RestController
@RequestMapping("/api/court")
public class CourtRestController {

    private final CourtFacade courtFacade;

    public CourtRestController(CourtFacade courtFacade) {
        this.courtFacade = courtFacade;
    }

    @Operation(summary = "Create a new court")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Court created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid court data provided", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CourtDto> create(@RequestBody @Valid CreateCourtDto dto) {
        CourtDto created = courtFacade.create(dto);
        return ResponseEntity.created(URI.create("/court/" + created.getId())).body(created);
    }

    @Operation(summary = "Get all courts")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "List of courts retrieved successfully")})
    @GetMapping
    public ResponseEntity<List<CourtDto>> findAll() {
        return ResponseEntity.ok(courtFacade.findAll());
    }

    @Operation(summary = "Get a court by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Court found"),
        @ApiResponse(responseCode = "404", description = "Court not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourtDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(courtFacade.findById(id));
    }

    @Operation(summary = "Update a court")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Court updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid court data provided", content = @Content),
        @ApiResponse(responseCode = "404", description = "Court not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CourtDto> update(@PathVariable Long id, @RequestBody @Valid CreateCourtDto dto) {
        CourtDto updated = courtFacade.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a court")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Court deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Court not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courtFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
