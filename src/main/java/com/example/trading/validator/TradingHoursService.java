package com.example.trading.validator;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class TradingHoursService {

    public static final ZoneId JAPAN_ZONE = ZoneId.of("Asia/Tokyo");

    public void validate(LocalDateTime now) {

        DayOfWeek day = now.getDayOfWeek();
        LocalTime time = now.toLocalTime();

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("土日は取引できません。");
        }

        if (time.isBefore(LocalTime.of(9, 0))) {
            throw new IllegalArgumentException("取引時間外です。");
        }

        if (time.isAfter(LocalTime.of(11, 30))
                && time.isBefore(LocalTime.of(12, 30))) {
            throw new IllegalArgumentException("取引時間外です。");
        }

        if (time.isAfter(LocalTime.of(15, 30))) {
            throw new IllegalArgumentException("取引時間外です。");
        }
    }
}