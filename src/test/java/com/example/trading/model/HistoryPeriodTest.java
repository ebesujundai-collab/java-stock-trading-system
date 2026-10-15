package com.example.trading.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HistoryPeriodTest {
    @ParameterizedTest
    @CsvSource({"all, ALL", "today, TODAY", "yesterday, YESTERDAY"})
    void mapsUserInputToEnum(String input, HistoryPeriod expected) {
        assertEquals(expected, HistoryPeriod.fromInput(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"ALL", "TODAY", "YESTERDAY", "Today", "all ", " today", "week", "1"})
    void rejectsOtherPeriods(String input) {
        assertThrows(IllegalArgumentException.class, () -> HistoryPeriod.fromInput(input));
    }
}
