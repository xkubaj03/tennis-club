package com.inqool.tennisclub.api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourtDto {

    @NotNull(message = "Court number name is required")
    private Integer courtNumber;

    @NotNull(message = "Surface ID is required")
    private Long surfaceId;
}
