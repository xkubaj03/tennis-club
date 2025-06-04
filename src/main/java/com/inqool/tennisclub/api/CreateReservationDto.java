package com.inqool.tennisclub.api;

import com.inqool.tennisclub.data.model.GameType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationDto {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotNull(message = "Game type is required")
    private GameType gameType;

    @NotNull(message = "Court number is required")
    private Integer courtNumber;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private OffsetDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private OffsetDateTime endTime;

    @AssertTrue(message = "End time must be after start time and reservation must be at least 30 minutes long")
    @Schema(hidden = true)
    public boolean isValidTimeRange() {
        if (endTime == null || startTime == null) {
            return true;
        }

        return endTime.isAfter(startTime) && startTime.plusMinutes(30).isBefore(endTime.plusSeconds(1));
    }

    @AssertTrue(message = "Reservation cannot be longer than 24 hours")
    @Schema(hidden = true)
    public boolean isNotTooLong() {
        if (endTime == null || startTime == null) {
            return true;
        }

        return startTime.plusHours(24).isAfter(endTime)
                || startTime.plusHours(4).equals(endTime);
    }
}
