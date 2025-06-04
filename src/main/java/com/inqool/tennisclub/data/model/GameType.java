package com.inqool.tennisclub.data.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;

public enum GameType {
    SINGLES("SINGLES"),
    DOUBLES("DOUBLES");

    private final String value;

    GameType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public BigDecimal getPriceMultiplier() {
        return this == DOUBLES ? BigDecimal.valueOf(1.5) : BigDecimal.valueOf(1.0);
    }
}
