package com.inqool.tennisclub.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inqool.tennisclub.data.model.GameType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {

    private Long id;

    private Long courtId;

    private String phoneNumber;

    private String customerName;

    private Integer courtNumber;

    private GameType gameType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private OffsetDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private OffsetDateTime endTime;

    private BigDecimal totalPrice;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private OffsetDateTime createdAt;
}
