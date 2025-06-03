package com.inqool.tennisclub.api;

import java.math.BigDecimal;

public record CourtSurfaceDto(Long id, String surfaceName, String surfaceDescription, BigDecimal costPerMinute) {}
