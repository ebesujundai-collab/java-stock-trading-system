package com.example.trading.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TradingHoursServiceTest {
    private final TradingHoursService service = new TradingHoursService();

    @ParameterizedTest
    @CsvSource({
            "08:59:59, false",
            "09:00:00, true",
            "09:00:00.000000001, true",
            "11:29:59.999999999, true",
            "11:30:00, true",
            "11:30:00.000000001, false",
            "11:30:01, false",
            "12:00:00, false",
            "12:29:59, false",
            "12:29:59.999999999, false",
            "12:30:00, true",
            "15:30:00, true",
            "15:30:00.000000001, false",
            "15:30:01, false",
            "00:00:00, false",
            "23:59:59.999999999, false"
    })
    void checksInclusiveBoundariesWithoutDroppingNanoseconds(String input, boolean expected) {
        assertEquals(expected, service.isTradingTime(LocalTime.parse(input)));
    }

    @ParameterizedTest
    @CsvSource({
            "2026-10-05, true", "2026-10-06, true", "2026-10-07, true",
            "2026-10-08, true", "2026-10-09, true", "2026-10-10, false",
            "2026-10-11, false"
    })
    void acceptsWeekdaysAndRejectsBothWeekendDays(String date, boolean expected) {
        assertEquals(expected, service.isTradingDay(LocalDate.parse(date)));
    }

    @Test
    void weekdayPublicHolidayIsStillATradingDay() {
        LocalDateTime newYearsDay = LocalDateTime.of(2026, 1, 1, 9, 0);
        assertTrue(service.isTradingDay(newYearsDay.toLocalDate()));
        assertDoesNotThrow(() -> service.validate(newYearsDay));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026-10-07T09:00:00", "2026-10-07T11:30:00",
            "2026-10-07T12:30:00", "2026-10-07T15:30:00"})
    void validatesWeekdaySessionBoundaries(String dateTime) {
        assertDoesNotThrow(() -> service.validate(LocalDateTime.parse(dateTime)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026-10-10T09:00:00", "2026-10-11T09:00:00",
            "2026-10-07T08:59:59", "2026-10-07T12:00:00", "2026-10-07T15:30:01"})
    void validationRejectsWeekendOrClosedSession(String dateTime) {
        assertThrows(IllegalArgumentException.class,
                () -> service.validate(LocalDateTime.parse(dateTime)));
    }

    @Test
    void validatesDayBeforeTimeToGiveTheRelevantError() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.validate(LocalDateTime.of(2026, 10, 10, 23, 0)));
        assertTrue(error.getMessage().contains("土曜日・日曜日"));
    }
}
