package com.inqool.tennisclub.api.court;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourtSurfaceDto {
    @NotBlank(message = "Surface name is required")
    private String surfaceName;

    @NotBlank(message = "Name is required")
    private String surfaceDescription;

    @NotNull(message = "Cost per minute is required")
    private Double costPerMinute;
}
