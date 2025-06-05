package com.inqool.tennisclub.rest;

import com.inqool.tennisclub.api.CourtSurfaceDto;
import com.inqool.tennisclub.api.CreateCourtSurfaceDto;
import com.inqool.tennisclub.facade.CourtSurfaceFacade;
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

@Tags(value = @Tag(name = "CourtSurface API"))
@RestController
@RequestMapping("/api/courtsurface")
public class CourtSurfaceRestController {
    private final CourtSurfaceFacade courtSurfaceFacade;

    public CourtSurfaceRestController(CourtSurfaceFacade courtSurfaceFacade) {
        this.courtSurfaceFacade = courtSurfaceFacade;
    }

    @Operation(summary = "Create a new CourtSurface")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "CourtSurface created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid CourtSurface data provided", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CourtSurfaceDto> create(@RequestBody @Valid CreateCourtSurfaceDto dto) {
        CourtSurfaceDto created = courtSurfaceFacade.create(dto);
        return ResponseEntity.created(URI.create("/courtsurface/" + created.getId()))
                .body(created);
    }

    @Operation(summary = "Get all CourtSurfaces")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "List of CourtSurfaces retrieved successfully")})
    @GetMapping
    public ResponseEntity<List<CourtSurfaceDto>> findAll() {
        return ResponseEntity.ok(courtSurfaceFacade.findAll());
    }

    @Operation(summary = "Get a CourtSurface by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CourtSurface found"),
        @ApiResponse(responseCode = "404", description = "CourtSurface not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourtSurfaceDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(courtSurfaceFacade.findById(id));
    }

    @Operation(summary = "Update a CourtSurface")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CourtSurface updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid CourtSurface data provided", content = @Content),
        @ApiResponse(responseCode = "404", description = "CourtSurface not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CourtSurfaceDto> update(
            @PathVariable Long id, @RequestBody @Valid CreateCourtSurfaceDto dto) {
        CourtSurfaceDto updated = courtSurfaceFacade.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a CourtSurface")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "CourtSurface deleted successfully"),
        @ApiResponse(responseCode = "404", description = "CourtSurface not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courtSurfaceFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
