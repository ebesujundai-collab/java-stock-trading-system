package com.example.trading.service;

import com.example.trading.validator.TradingHoursService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TradingHoursServiceTest {

    private final TradingHoursService service =
            new TradingHoursService();

    @ParameterizedTest
    @ValueSource(strings = {
            "2026-10-07T09:00:00",
            "2026-10-07T11:30:00",
            "2026-10-07T12:30:00",
            "2026-10-07T15:30:00"
    })
    void acceptsTradingTimeBoundaries(String dateTime) {
        assertDoesNotThrow(
                () -> service.validate(
                        LocalDateTime.parse(dateTime)
                )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2026-10-07T08:59:59",
            "2026-10-07T11:30:00.000000001",
            "2026-10-07T12:00:00",
            "2026-10-07T12:29:59",
            "2026-10-07T15:30:00.000000001",
            "2026-10-07T15:30:01"
    })
    void rejectsOutsideTradingHours(String dateTime) {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.validate(
                        LocalDateTime.parse(dateTime)
                )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2026-10-10T09:00:00",
            "2026-10-11T09:00:00"
    })
    void rejectsSaturdayAndSunday(String dateTime) {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.validate(
                        LocalDateTime.parse(dateTime)
                )
        );
    }

    @Test
    void weekendErrorMessageSaysTradingIsNotAllowed() {
        IllegalArgumentException error =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.validate(
                                LocalDateTime.of(
                                        2026, 10, 10, 10, 0
                                )
                        )
                );

        assertTrue(
                error.getMessage().contains("土日は取引できません")
        );
    }

    @Test
    void weekdayHolidayIsAcceptedBecauseHolidaysAreNotChecked() {
        assertDoesNotThrow(
                () -> service.validate(
                        LocalDateTime.of(
                                2026, 1, 1, 9, 0
                        )
                )
        );
    }
}
