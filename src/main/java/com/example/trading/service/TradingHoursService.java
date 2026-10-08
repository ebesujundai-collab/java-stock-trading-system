package com.example.trading.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/** 日本時間として渡された日時について、曜日と取引時間を判定する。 */
public class TradingHoursService {
    public static final ZoneId JAPAN_ZONE = ZoneId.of("Asia/Tokyo");

    private static final LocalTime MORNING_START = LocalTime.of(9, 0);
    private static final LocalTime MORNING_END = LocalTime.of(11, 30);
    private static final LocalTime AFTERNOON_START = LocalTime.of(12, 30);
    private static final LocalTime AFTERNOON_END = LocalTime.of(15, 30);

    public boolean isTradingDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    public boolean isTradingTime(LocalTime time) {
        return isWithin(time, MORNING_START, MORNING_END)
                || isWithin(time, AFTERNOON_START, AFTERNOON_END);
    }

    private boolean isWithin(LocalTime time, LocalTime start, LocalTime end) {
        // 「開始より前でない、かつ終了より後でない」で両端を含める。
        return !time.isBefore(start) && !time.isAfter(end);
    }

    public void validate(LocalDateTime dateTime) {
        if (!isTradingDay(dateTime.toLocalDate())) {
            throw new IllegalArgumentException("土曜日・日曜日は取引できません。");
        }
        if (!isTradingTime(dateTime.toLocalTime())) {
            throw new IllegalArgumentException(
                    "取引可能時間は日本時間の09:00:00〜11:30:00、12:30:00〜15:30:00です。");
        }
    }
}
