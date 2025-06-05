package com.inqool.tennisclub.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourtDto {

    private Long id;

    private Integer courtNumber;

    private Long surfaceId;
}
