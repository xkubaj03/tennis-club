package com.inqool.tennisclub.api.court;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourtSurfaceDto {
    private Long id;

    private String surfaceName;

    private String surfaceDescription;

    private BigDecimal costPerMinute;
}
